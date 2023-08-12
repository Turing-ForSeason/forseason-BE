package com.turing.forseason.controller;

import com.turing.forseason.dto.StompMessage;
import com.turing.forseason.dto.TalkRoom;
import com.turing.forseason.entity.TalkEntity;
import com.turing.forseason.entity.UserEntity;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.service.TalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TalkAPIController {
    private final TalkService talkService;

    //테스트코드
    @GetMapping("/talk/rooms")
    public ApplicationResponse<List<TalkRoom>> getTalkRoomList(@RequestParam String userId){
        System.out.println("getTalkRoomList, userId: " + userId);
        // JWT 토큰으로 유효성 검사 필수 (나중에 합칠때 구현 예정)
        // 지금은 HttpSession으로 구현함
        if (userId == null) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, talkService.findAllRoom());
    }

//    //기존코드
//    @GetMapping("/talk/rooms")
//    public ApplicationResponse<List<TalkRoom>> getTalkRoomList(HttpSession session){
//        System.out.println(session.getId());
//        System.out.println("roomList");
//        // JWT 토큰으로 유효성 검사 필수 (나중에 합칠때 구현 예정)
//        // 지금은 HttpSession으로 구현함
//        System.out.println(session.getAttribute("userId"));
//        if (session.getAttribute("userId") == null) {
//            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
//        }
//
//        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, talkService.findAllRoom());
//    }

    @GetMapping("/talk/room")
    public ApplicationResponse<String> enterTalkRoom(@RequestParam("location") String location, String userId){
        // JWT 토큰으로 유효성 검사 필수 (나중에 합칠때 구현 예정)
        // 지금은 HttpSession으로 구현함
        // 여기서 채팅방에 유저 추가 + return UUID & TalkRoom
        System.out.println("enter");
        if (userId==null) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        String userUUID = UUID.randomUUID().toString();
        System.out.println("userId, " + userId+", userUUID: "+userUUID);



        // 채팅방에 유저 추가
        talkService.addUser(location, userUUID, Integer.parseInt(userId));
        System.out.println("enterTalkRoom : "+Integer.parseInt(userId));

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, userUUID);
    }


//    //기존코드
//    // 채팅방 입장
//    // 파라미터로 넘어오는 location을 확인후 해당 location을 기준으로
//    // talkRoom에 사용자 추가 & STOMP 통신에 사용할 userUUID 발급
//    // 구현상 불가피하게 여기서 addUser를 실행.
//    @GetMapping("/talk/room")
//    public ApplicationResponse<String> enterTalkRoom(@RequestParam("location") String location, HttpSession session){
//        // JWT 토큰으로 유효성 검사 필수 (나중에 합칠때 구현 예정)
//        // 지금은 HttpSession으로 구현함
//        // 여기서 채팅방에 유저 추가 + return UUID & TalkRoom
//        if (session.getAttribute("userId") == null) {
//            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
//        }
//
//        String userUUID = UUID.randomUUID().toString();
//        int userId = (int)session.getAttribute("userId");
//        System.out.println("userId = " + userId);
//
//        // 채팅방에 유저 추가
//        talkService.addUser(location, userUUID, userId);
//
//        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, userUUID);
//    }


    //userNickname과 userProfilePicture를 DB에서 가져와서 초기화 해주기
    @GetMapping("/talk/user/init")
    @ResponseBody
    public ApplicationResponse<Map<String, String>> initUser(@RequestParam("location") String location, @RequestParam("userUUID") String userUUID) {
        System.out.println("initUser, location: "+location+", userUUID: "+userUUID);

        UserEntity user = talkService.getUser(location, userUUID);
        Map<String, String> response = new HashMap<>();

        response.put("userNickname", user.getUserNickname());
        response.put("userProfilePicture", user.getUserProfilePicture());

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, response);
    }


    //이전 기록들 불러오기
    @GetMapping("/talk/talks")
    @ResponseBody
    public ApplicationResponse<List<StompMessage>> getTalks(@RequestParam("page") int page, @RequestParam("location") String location,
                                                            @RequestParam("userUUID") String userUUID) {
        System.out.println("getTalks, page: " + page + ", location: " + location + ", userUUID: " + userUUID);
        List<TalkEntity> talkList = talkService.getTalks(location, page);
        List<StompMessage> stompMessageList = talkService.talk2Messages(location, userUUID, talkList);

        int i = 0;
        for (StompMessage item : stompMessageList) {
            System.out.println("item " + i + ": " + item);
            i+=1;
        }

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, stompMessageList);
    }


    //유저수를 갱신하는 역할.
    @GetMapping("/talk/room/userCount")
    public ApplicationResponse<Map<String, Long>> getUserCount(@RequestParam("location") String location) {
//        TalkRoom talkRoom = TalkRooms.getInstance().getTalkRoomMap().get(location);
        System.out.println("getUserCount, location: "+location);
        TalkRoom talkRoom = talkService.findByLocation(location);

        Map<String, Long> response = new HashMap<>();
        response.put("userCount", talkRoom.getUserCount());

//        return ResponseEntity.ok(response);
        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, response);
    }


    //enterTalkRoom에서 구현상 불가피하게 STOMP소켓 연결전에 UserList에 유저를 추가했으므로,
    //만일 STOMP 연결 실패시 해당 사용자 삭제.
    @PostMapping("/talk/user/delete")
    public void deleteUser(@RequestBody Map<String,String> userInfo) {
        System.out.println("delete user, "+userInfo);
        //소켓 연결 오류시 userList 에서 user 삭제.
        String location = userInfo.get("location");
        String userUUID = userInfo.get("userUUID");

        talkService.delUser(location, userUUID);
    }
}
