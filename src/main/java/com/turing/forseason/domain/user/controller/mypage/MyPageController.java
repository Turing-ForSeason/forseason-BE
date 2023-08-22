package com.turing.forseason.domain.user.controller.mypage;

import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.service.UserService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final UserService userService;

    @GetMapping("/mypage/{userId}")
    public ApplicationResponse<UserEntity> getUser(@PathVariable long userId, HttpServletRequest request) {
        //마이페이지 보기

        UserEntity user = userService.getUser(request);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, user);
    }

}