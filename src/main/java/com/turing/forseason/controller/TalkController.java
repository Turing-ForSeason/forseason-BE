package com.turing.forseason.controller;

import com.turing.forseason.dto.MessageDTO;
import com.turing.forseason.entity.TalkEntity;
import com.turing.forseason.entity.UserEntity;
import com.turing.forseason.service.TalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class TalkController {
    private final SimpMessageSendingOperations tmp;
    private final TalkService talkService;

    @MessageMapping("/talk/enter")
    public void enterUser(@Payload MessageDTO messageDTO, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("enterUser");
        System.out.println("messageDTO = " + messageDTO);

        String location = messageDTO.getLocation();
        String userUUID = messageDTO.getUserUUID();
        String sessionId = headerAccessor.getSessionId();

        //소켓 세션에 유저 정보 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("location", location);

        //type=ENTER 인 메세지 발송 (이거로 userCount 갱신 예정.)
        tmp.convertAndSend("/sub/talk/room/"+ messageDTO.getLocation(),messageDTO);
    }

    // 리액트 환경 때문에 추가.
    // 필요없을 확률 매우 높음.
//    @MessageMapping("/talk/leave")
//    public void leaveUser(@Payload MessageDTO messageDTO, SimpMessageHeaderAccessor headerAccessor){
//        System.out.println("leaveUser");
//        System.out.println("messageDTO = " + messageDTO);
//
//        String location = messageDTO.getLocation();
//        String userUUID = messageDTO.getUserUUID();
//
//        //채팅방 유저리스트에서 삭제(by UUID)
//        talkService.delUser(location,userUUID);
//
//        //type=LEAVE 인 메세지 발송 (이거로 userCount 갱신 예정.)
//        tmp.convertAndSend("/sub/talk/room/"+ messageDTO.getLocation(),messageDTO);
//    }

    @MessageMapping("/talk/sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO){
        System.out.println("sendMessage");
        System.out.println("messageDTO = " + messageDTO);

        messageDTO.setDate(LocalDateTime.now());
        talkService.storeTalkEntity(messageDTO);
        tmp.convertAndSend("/sub/talk/room/"+ messageDTO.getLocation(),messageDTO);
    }

    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String location = (String) headerAccessor.getSessionAttributes().get("location");

        //채팅방 유저리스트에서 삭제(by UUID)
        talkService.delUser(location,userUUID);

        MessageDTO messageDTO = MessageDTO.builder().type(MessageDTO.MessageType.LEAVE).build();

        //type=LEAVE 인 메세지 발송 (이거로 userCount 갱신 예정.)
        tmp.convertAndSend("/sub/talk/room/" + location, messageDTO);
    }

    //userNickname과 userProfilePicture를 DB에서 가져와서 초기화 해주기
    @GetMapping("/initUser")
    @ResponseBody
    public ResponseEntity<?> initUser(@RequestParam("location") String location, @RequestParam("userUUID") String userUUID) {
        System.out.println("initUser");
        System.out.println("location : " + location);
        System.out.println("userUUID : " + userUUID);

        UserEntity user = talkService.getUser(location, userUUID);

        Map<String, String> response = new HashMap<>();

        response.put("userNickname", user.getUserNickname());
        response.put("userProfilePicture", user.getUserProfilePicture());

        return ResponseEntity.ok(response);
    }

    //이전 기록들 불러오기
    @GetMapping("/getTalks")
    @ResponseBody
    public ResponseEntity<?> getTalks(@RequestParam("page") int page, @RequestParam("location") String location,
                                      @RequestParam("userUUID") String userUUID) {
        System.out.println("getTalks");
        System.out.println("page = " + page + ", location = " + location + ", userUUID = " + userUUID);
        List<TalkEntity> talkList = talkService.getTalks(location, page);
        List<MessageDTO> messageList = talkService.talk2Messages(location, userUUID, talkList);

        return ResponseEntity.ok(messageList);
    }
}
