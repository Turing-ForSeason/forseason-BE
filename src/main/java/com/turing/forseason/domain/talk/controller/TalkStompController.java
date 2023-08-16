package com.turing.forseason.domain.talk.controller;

import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.global.dto.StompResponse;
import com.turing.forseason.global.errorException.StompErrorCode;
import com.turing.forseason.domain.talk.service.TalkService;
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

    // 채팅방 입장시
    @MessageMapping("/talk/enter")
    public void enterUser(@Payload StompMessage stompMessage, SimpMessageHeaderAccessor headerAccessor){

        String location = stompMessage.getLocation();
        String userUUID = stompMessage.getUserUUID();

        //소켓 세션에 유저 정보 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("location", location);

        //type=ENTER 인 메세지 발송 (이거로 클라이언트가 userCount 갱신.)
        tmp.convertAndSend("/sub/talk/room/" + stompMessage.getLocation(), StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }

    // 채팅방 퇴장시
    @MessageMapping("/talk/leave")
    public void leaveUser(@Payload StompMessage stompMessage, SimpMessageHeaderAccessor headerAccessor) {
        String location = stompMessage.getLocation();
        String userUUID = stompMessage.getUserUUID();

        // userList에서 사용자 삭제
        talkService.delUser(location,userUUID);

        //type=LEAVE 인 메세지 발송 (이거로 클라이언트가 userCount 갱신.)
        tmp.convertAndSend("/sub/talk/room/" + location, StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }

    // 메세지 전송
    @MessageMapping("/talk/sendMessage")
    public void sendMessage(@Payload StompMessage stompMessage){

        // 서버에서 date를 추가
        stompMessage.setDate(LocalDateTime.now());

        // 메세지를 db에 저장
        talkService.storeTalkEntity(stompMessage);

        // 채팅방에 메세지 전송
        tmp.convertAndSend("/sub/talk/room/"+ stompMessage.getLocation(), StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }

    // 연결 해제시 채팅방 리스트에서 사용자를 삭제하는 로직을 3중으로 구현함.(@MessageMapping("/talk/leave"), @PostMapping("/talk/user/delete"), 그리고 이 메서드)
    // 이 메서드는 DISCONNECT 포멧의 STOMP 메세지를 수신했을 때 실행
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String location = (String) headerAccessor.getSessionAttributes().get("location");

        //채팅방 유저리스트에서 삭제(by UUID)
        talkService.delUser(location,userUUID);

        StompMessage stompMessage = StompMessage.builder().type(StompMessage.MessageType.LEAVE).build();

        //type=LEAVE 인 메세지 발송 (이거로 userCount 갱신 예정.)
        tmp.convertAndSend("/sub/talk/room/" + location, StompResponse.ok(StompErrorCode.SUCCESS, stompMessage));
    }


}
