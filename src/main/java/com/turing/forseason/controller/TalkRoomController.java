package com.turing.forseason.controller;

import com.turing.forseason.dto.TalkRoom;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorExeption.CustomExeption;
import com.turing.forseason.global.errorExeption.ErrorCode;
import com.turing.forseason.mapper.TalkRooms;
import com.turing.forseason.service.TalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TalkRoomController {
    private final TalkService talkService;

    @GetMapping("/talk/roomList")
    public ApplicationResponse<List<TalkRoom>> getTalkRoomList(HttpSession session){
        // JWT 토큰으로 유효성 검사 필수 (나중에 합칠때 구현 예정)
        // 지금은 HttpSession으로 구현함
        if (session.getAttribute("userId") == null) {
            throw new CustomExeption(ErrorCode.INVALID_JWT_TOKEN);
        }

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, talkService.findAllRoom());
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 location을 확인후 해당 location을 기준으로
    // 채팅방을 찾아서 클라이언트를 talkroom 으로 보낸다.
    // 구현상 불가피하게 여기서 addUser를 실행.
    @GetMapping("/talk/room")
    public ApplicationResponse<String> enterTalkRoom(@RequestParam("location") String location, HttpSession session){
        // JWT 토큰으로 유효성 검사 필수 (나중에 합칠때 구현 예정)
        // 지금은 HttpSession으로 구현함
        // 여기서 채팅방에 유저 추가 + return UUID & TalkRoom
        if (session.getAttribute("userId") == null) {
            throw new CustomExeption(ErrorCode.INVALID_JWT_TOKEN);
        }

        String userUUID = UUID.randomUUID().toString();
        int userId = (int)session.getAttribute("userId");
        System.out.println("userId = " + userId);

        // 채팅방에 유저 추가
        talkService.addUser(location, userUUID, userId);

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, userUUID);
    }

    //Ajax 와 매핑된 메서드. 유저수를 갱신하는 역할.
    @GetMapping("/talk/userCount")
    public ApplicationResponse<Map<String, Long>> getUserCount(@RequestParam("location") String location) {
//        TalkRoom talkRoom = TalkRooms.getInstance().getTalkRoomMap().get(location);
        TalkRoom talkRoom = talkService.findByLocation(location);

        Map<String, Long> response = new HashMap<>();
        response.put("userCount", talkRoom.getUserCount());

//        return ResponseEntity.ok(response);
        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, response);
    }

    //roomDetail에서 구현상 불가피하게 STOMP소켓 연결전에 UserList에 유저를 추가했으므로,
    //만일 STOMP 연결 실패시 Ajax통신으로 해당 사용자 삭제.
    @PostMapping("/talk/deleteUser")
    public void deleteUser(@RequestBody Map<String,String> userInfo) {
        //소켓 연결 오류시 userList 에서 user 삭제.
        String location = userInfo.get("location");
        String userUUID = userInfo.get("userUUID");

        talkService.delUser(location, userUUID);
    }
}
