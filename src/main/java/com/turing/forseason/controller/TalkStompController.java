package com.turing.forseason.controller;

import com.turing.forseason.dto.StompMessage;
import com.turing.forseason.global.dto.StompResponse;
import com.turing.forseason.global.errorException.StompErrorCode;
import com.turing.forseason.service.TalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class TalkStompController {
    private final SimpMessageSendingOperations tmp;
    private final TalkService talkService;

    @MessageMapping("/talk/enter")
    public void enterUser(@Payload StompMessage stompMessage, SimpMessageHeaderAccessor headerAccessor){

        String location = stompMessage.getLocation();
        String userUUID = stompMessage.getUserUUID();

        System.out.println("STOMP enter, location: "+location+", userUUID: "+userUUID);
        //소켓 세션에 유저 정보 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("location", location);

        //type=ENTER 인 메세지 발송 (이거로 userCount 갱신 예정.)
        tmp.convertAndSend("/sub/talk/room/" + stompMessage.getLocation(), StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }

    @MessageMapping("/talk/leave")
    public void leaveUser(@Payload StompMessage stompMessage, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("leaveUser");
        String location = stompMessage.getLocation();
        String userUUID = stompMessage.getUserUUID();

        talkService.delUser(location,userUUID);
        tmp.convertAndSend("/sub/talk/room/" + location, StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }

    @MessageMapping("/talk/sendMessage")
    public void sendMessage(@Payload StompMessage stompMessage){
        System.out.println("sendMessage");
        System.out.println("stompMessage = " + stompMessage);

        stompMessage.setDate(LocalDateTime.now());
        talkService.storeTalkEntity(stompMessage);
        tmp.convertAndSend("/sub/talk/room/"+ stompMessage.getLocation(), StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }

    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String location = (String) headerAccessor.getSessionAttributes().get("location");
        System.out.println("disconnect, location: "+location+", userUUID: "+userUUID);

        //채팅방 유저리스트에서 삭제(by UUID)
        talkService.delUser(location,userUUID);

        StompMessage stompMessage = StompMessage.builder().type(StompMessage.MessageType.LEAVE).build();

        //type=LEAVE 인 메세지 발송 (이거로 userCount 갱신 예정.)
        tmp.convertAndSend("/sub/talk/room/" + location, StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }


}
