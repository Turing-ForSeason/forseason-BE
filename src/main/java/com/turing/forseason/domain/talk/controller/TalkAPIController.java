package com.turing.forseason.domain.talk.controller;

import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.domain.talk.dto.TalkRoom;
import com.turing.forseason.domain.talk.entity.TalkEntity;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.domain.talk.service.TalkService;
import com.turing.forseason.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TalkAPIController {
    private final TalkService talkService;

    // 채팅방 리스트 반환
    @GetMapping("/talk/rooms")
    public ApplicationResponse<List<TalkRoom>> getTalkRoomList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser() == null) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, talkService.findAllRoom());
    }


    // 채팅방 입장
    // 파라미터로 넘어오는 location을 확인후 해당 location을 기준으로
    // talkRoom에 사용자 추가 & STOMP 통신에 사용할 userUUID 발급
    // 구현상 불가피하게 여기서 addUser를 실행.
    @GetMapping("/talk/room")
    public ApplicationResponse<String> enterTalkRoom(@RequestParam("location") String location, @AuthenticationPrincipal PrincipalDetails principalDetails){
        // 여기서 채팅방에 유저 추가 + return UUID & TalkRoom
        if (principalDetails.getUser() == null) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        String userUUID = UUID.randomUUID().toString();
        Long userId = principalDetails.getUser().getUserId();
        System.out.println("userId = " + userId);

        // 채팅방에 유저 추가
        talkService.addUser(location, userUUID, userId);

        return ApplicationResponse.ok(ErrorCode.TALK_SUCCESS, userUUID);
    }


    //userNickname과 userProfilePicture를 DB에서 가져와서 초기화 해주기
    @GetMapping("/talk/user/init")
    @ResponseBody
    public ApplicationResponse<Map<String, String>> initUser(@RequestParam("location") String location, @RequestParam("userUUID") String userUUID) {
        System.out.println("initUser, location: "+location+", userUUID: "+userUUID);

        UserEntity user = talkService.getUser(location, userUUID);
        Map<String, String> response = new HashMap<>();

        response.put("userNickname", user.getUserNickname());
        response.put("userProfilePicture", user.getThumbnail());

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
