package com.turing.forseason.domain.talk.controller;

import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.domain.talk.dto.TalkDto;
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
    public ApplicationResponse<List<TalkRoom>> getTalkRoomList() {

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, talkService.findAllRoom());
    }


    // 채팅방 입장
    // 파라미터로 넘어오는 location을 확인후 해당 location을 기준으로
    // talkRoom에 사용자 추가 & STOMP 통신에 사용할 userUUID 발급
    // 구현상 불가피하게 여기서 addUser를 실행.
    @GetMapping("/talk/room")
    public ApplicationResponse<String> enterTalkRoom(@RequestParam("location") String location, @AuthenticationPrincipal PrincipalDetails principalDetails){
        // 여기서 채팅방에 유저 추가 + return UUID & TalkRoom
        String userUUID = UUID.randomUUID().toString();
        Long userId = principalDetails.getUser().getUserId();

        // 채팅방에 유저 추가
        talkService.addUser(location, userUUID, userId);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, userUUID);
    }


    //userNickname과 userProfilePicture를 DB에서 가져와서 초기화 해주기
    @GetMapping("/talk/user/init")
    @ResponseBody
    public ApplicationResponse<Map<String, String>> initUser(@RequestParam("location") String location, @RequestParam("userUUID") String userUUID) {

        UserEntity user = talkService.getUser(location, userUUID);
        Map<String, String> response = new HashMap<>();

        response.put("userNickname", user.getUserNickname());
        response.put("userProfilePicture", user.getThumbnail());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, response);
    }


    //이전 기록들 불러오기
    @GetMapping("/talk/talks")
    @ResponseBody
    public ApplicationResponse<List<StompMessage>> getTalks(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("location") String location,
                                                            @RequestParam("userUUID") String userUUID) {

        List<TalkEntity> talkList = talkService.getTalks(location, page, size);
        List<StompMessage> stompMessageList = talkService.talk2Messages(location, userUUID, talkList);


        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, stompMessageList);
    }


    //유저수를 갱신하는 역할.
    @GetMapping("/talk/room/userCount")
    public ApplicationResponse<Map<String, Long>> getUserCount(@RequestParam("location") String location) {

        TalkRoom talkRoom = talkService.findByLocation(location);

        Map<String, Long> response = new HashMap<>();
        response.put("userCount", talkRoom.getUserCount());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, response);
    }


    //enterTalkRoom에서 구현상 불가피하게 STOMP소켓 연결전에 UserList에 유저를 추가했으므로,
    //만일 STOMP 연결 실패시 해당 사용자 삭제.
    // + 사용자 삭제는 인증 없이도 할 수 있도록 SecurityConfig에 설정.
    @PostMapping("/talk/user/delete")
    public void deleteUser(@RequestBody Map<String,String> userInfo) {

        String location = userInfo.get("location");
        String userUUID = userInfo.get("userUUID");

        talkService.delUser(location, userUUID);
    }

    // 커뮤니티 페이지에서 talk 리스트 렌더링용
    @GetMapping("/talk/talklist")
    public ApplicationResponse<List<TalkDto>> getTalkList(@RequestParam(name = "size") int size){
        List<TalkEntity> talkEntityList = talkService.getTalkForCommunityPage(0, size);
        List<TalkDto> talkDtoList = talkService.convert2TalkDto(talkEntityList);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, talkDtoList);
    }
}
