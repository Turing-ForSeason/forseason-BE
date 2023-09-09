package com.turing.forseason.domain.user.controller.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turing.forseason.domain.user.dto.SignInRequestDto;
import com.turing.forseason.domain.user.dto.SignUpRequestDto;
import com.turing.forseason.domain.user.dto.UserDetailDto;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.service.UserService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtTokenProvider;
import com.turing.forseason.global.jwt.OauthToken;
import com.turing.forseason.global.jwt.PrincipalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/login/oauth2/code/kakao")
    public ApplicationResponse<String> Login(@RequestParam("code") String code) {

        // 카카오로부터 OauthToken 발급받기
        OauthToken oauthToken = userService.getKakaoAccessToken(code);
        // 발급 받은 OauthToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        System.out.println(oauthToken);
        String jwtToken = userService.saveUserAndGetToken(oauthToken);
        System.out.println("jwt 토큰 발급");
        System.out.println(jwtToken);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, JwtTokenProvider.TOKEN_PREFIX + jwtToken);
    }

    @GetMapping("/auth/me")
    public ApplicationResponse<UserDetailDto> getCurrentUser(HttpServletRequest request) {
        UserEntity user = userService.getUser(request);
        UserDetailDto userDetail = userService.getUserDetail(user);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, userDetail);
    }

    @PostMapping("/decodeToken")
    public String decodeToken(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 값을 가져옵니다.
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "{ \"error\": \"Bearer token missing\" }";
        }
        String token = authHeader.substring(7);
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String nickname = decodedJWT.getClaim("nickname").asString();
            String email = decodedJWT.getClaim("sub").asString();
            Long id = decodedJWT.getClaim("id").asLong();
            String json = "{ \"username\": \"" + nickname + "\", \"email\": \"" + email + "\", \"id\": \"" + id + "\" }";
            return json;
        } catch (Exception e) {
            return "{ \"error\": \"Token invalid\" }";
        }
    }


    @GetMapping("/logout/service")
    public ApplicationResponse<String> serviceLogout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 서비스 로그아웃 (토큰 만료시키기)
        System.out.println("서비스 로그아웃");
        userService.serviceLogout(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "서비스 로그아웃 되었습니다.");
    }


    // 여기서부터는 일반로그인
    @PostMapping("/signup/general")
    public ApplicationResponse<String> generalSignUp(@RequestBody SignUpRequestDto requestDto) {
        // 코드 리팩토링 필수(URI 설정, 비밀번호 암호화 등)
        // 검증 메일 로직 추가 예정.
        System.out.println("일반 회원가입");
        userService.signUpUser(requestDto);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입 성공");
    }

    @PostMapping("/signin/general")
    public ApplicationResponse<String> generalSignIn(@RequestBody SignInRequestDto requestDto) {
        System.out.println("일반 로그인");
        String jwtToken = userService.signInAndGetToken(requestDto);
        System.out.println(jwtToken);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, JwtTokenProvider.TOKEN_PREFIX + jwtToken);
    }
}

