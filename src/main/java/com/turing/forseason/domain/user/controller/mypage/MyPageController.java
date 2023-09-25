package com.turing.forseason.domain.user.controller.mypage;

import com.turing.forseason.domain.user.dto.UpdateUserInfoRequest;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.service.UserService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final UserService userService;

    @GetMapping("/mypage")
    public ApplicationResponse<UserEntity> getUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        //마이페이지 보기

        UserEntity user = userService.getUserById(principalDetails.getUser().getUserId());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, user);
    }

    @GetMapping("/mypage/editprofile")
    public ApplicationResponse<UserEntity> getBeforeEditUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        //회원정보 수정 보기

        UserEntity user = userService.getUserById(principalDetails.getUser().getUserId());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, user);
    }

    @PostMapping("/mypage/editprofile")
    public ApplicationResponse<UserEntity> editUser(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody UpdateUserInfoRequest request) {
        //회원정보 수정

        // UserEntity의 update 메서드 호출 등 업데이트 작업 수행
        UserEntity updatedUserInfo = userService.update(principalDetails.getUser().getUserId(), request);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, updatedUserInfo);
    }
}
