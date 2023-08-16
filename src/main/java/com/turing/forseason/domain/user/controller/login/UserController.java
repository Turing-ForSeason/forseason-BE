package com.turing.forseason.domain.user.controller.login;

import com.turing.forseason.domain.user.dto.UserDetailDto;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.service.UserService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/login/oauth2/code/kakao")
    public ApplicationResponse<String> Login(@RequestParam("code") String code) {

        // 카카오로부터 access 토큰 발급받기
        String oauthToken = userService.getKakaoAccessToken(code);

        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = userService.saveUserAndGetToken(oauthToken);
        System.out.println(jwtToken);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, JwtProperties.TOKEN_PREFIX + jwtToken);
    }

    @GetMapping("/auth/me")
    public ApplicationResponse<UserDetailDto> getCurrentUser(HttpServletRequest request) {
        UserEntity user = userService.getUser(request);
        UserDetailDto userDetail = userService.getUserDetail(user);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, userDetail);
    }

}

