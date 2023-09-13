package com.turing.forseason.domain.user.controller.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turing.forseason.domain.user.domain.KakaoProfile;
import com.turing.forseason.domain.user.dto.*;
import com.turing.forseason.domain.user.dto.auth.EmailVerificationDto;
import com.turing.forseason.domain.user.dto.auth.ReissueRequestDto;
import com.turing.forseason.domain.user.dto.auth.SignInRequestDto;
import com.turing.forseason.domain.user.dto.auth.SignUpRequestDto;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.service.AuthService;
import com.turing.forseason.domain.user.service.GeneralAuthService;
import com.turing.forseason.domain.user.service.KakaoAuthService;
import com.turing.forseason.domain.user.service.UserService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtTokenDto;
import com.turing.forseason.global.jwt.OauthToken;
import com.turing.forseason.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final GeneralAuthService generalAuthService;
    private final KakaoAuthService kakaoAuthService;
    private final AuthService authService;

    @GetMapping("/api/login/oauth2/code/kakao")
    public ApplicationResponse<JwtTokenDto> Login(@RequestParam("code") String code) {

        // 카카오로부터 OauthToken 발급받기
        OauthToken oauthToken = kakaoAuthService.getKakaoAccessToken(code);
        // 발급 받은 OauthToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        System.out.println(oauthToken);
        JwtTokenDto jwtTokenDto = kakaoAuthService.saveUserAndGetToken(oauthToken);
        System.out.println("jwt 토큰 발급");
        System.out.println(jwtTokenDto.getAccessToken());
        System.out.println(jwtTokenDto.getRefreshToken());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, jwtTokenDto);
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


    @PostMapping("/auth/logout")
    public ApplicationResponse<String> serviceLogout(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestBody JwtTokenDto jwtTokenDto) {
        // 로그아웃: JwtToken 만료시키기 & 카카오 로그인일 경우, OauthToken 또한 만료시키기.
        System.out.println("서비스 로그아웃");
        authService.deprecateTokens(jwtTokenDto);
        kakaoAuthService.serviceLogout(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "성공적으로 로그아웃되었습니다.");
    }


    // 여기서부터는 일반로그인
    @GetMapping("/signup/verification/email")
    public ApplicationResponse<String> getEmailAuthCode(@RequestParam(name = "email") String email) {
        if(generalAuthService.isDuplicatedEmail(email))
            generalAuthService.sendEmailAuthCode(email);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "인증 코드가 전송되었습니다.");
    }

    @PostMapping("/signup/verification/email")
    public ApplicationResponse<String> verifyEmail(@RequestBody EmailVerificationDto emailVerificationDto) {
        generalAuthService.verifyEmail(emailVerificationDto);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "인증에 성공하였습니다.");
    }

    @PostMapping("/signup/general")
    public ApplicationResponse<String> generalSignUp(@RequestBody SignUpRequestDto requestDto) {
        // 코드 리팩토링 필수(URI 설정, 비밀번호 암호화 등)
        System.out.println("일반 회원가입");
        if(generalAuthService.isDuplicatedEmail(requestDto.getUserEmail())) {
            generalAuthService.signUpUser(requestDto);
        }
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입 성공");
    }

    @PostMapping("/signin/general")
    public ApplicationResponse<JwtTokenDto> generalSignIn(@RequestBody SignInRequestDto requestDto) {
        System.out.println("일반 로그인");
        JwtTokenDto jwtTokenDto = generalAuthService.signInAndGetToken(requestDto);
        System.out.println(jwtTokenDto);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @PostMapping("/auth/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        JwtTokenDto jwtTokenDto = authService.reissue(reissueRequestDto.getRefreshToken());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }


    // 밑의 코드들은 전부 Test 용
    @GetMapping("test/oauthtoken/expried")
    public ApplicationResponse<String> test1(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserEntity user = principalDetails.getUser();
        boolean isExpired = kakaoAuthService.isExpired(user.getUserId());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "" + isExpired);
    }

    @GetMapping("test/oauthtoken/refresh")
    public ApplicationResponse<OauthToken> test2(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserEntity user = principalDetails.getUser();
        OauthToken newOauthToken = kakaoAuthService.getRefresh(user.getUserId());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, newOauthToken);
    }

    @GetMapping("test/oauthtoken/profile")
    public ApplicationResponse<KakaoProfile> test3(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserEntity user = principalDetails.getUser();
        OauthToken oauthToken = kakaoAuthService.getOauthToken(user.getUserId());

        KakaoProfile kakaoProfile = kakaoAuthService.findProfile(oauthToken);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, kakaoProfile);
    }
}

