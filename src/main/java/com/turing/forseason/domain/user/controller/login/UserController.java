package com.turing.forseason.domain.user.controller.login;

import com.turing.forseason.domain.user.dto.UserDetailDto;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.service.UserService;
import com.turing.forseason.global.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/login/oauth2/code/kakao")
    public ResponseEntity Login(@RequestParam("code") String code) {

        // 카카오로부터 access 토큰 발급받기
        String oauthToken = userService.getKakaoAccessToken(code);

        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = userService.saveUserAndGetToken(oauthToken);
        System.out.println(jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        System.out.println(headers);

        return ResponseEntity.ok().headers(headers).body("success");
    }
    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            UserEntity user = userService.getUser(request);
            UserDetailDto userDetail = userService.getUserDetail(user);
            return new ResponseEntity<>(userDetail, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}

