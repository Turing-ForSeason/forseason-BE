package com.turing.forseason.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.forseason.domain.user.domain.KakaoProfile;
import com.turing.forseason.domain.user.dto.auth.OauthTokenInfoRes;
import com.turing.forseason.domain.user.entity.LoginType;
import com.turing.forseason.domain.user.entity.Role;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtTokenDto;
import com.turing.forseason.global.jwt.JwtTokenProvider;
import com.turing.forseason.global.jwt.OauthToken;
import com.turing.forseason.global.jwt.PrincipalDetails;
import com.turing.forseason.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoAuthService {
    // 카카오 로그인 관련 로직
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    public OauthToken getKakaoAccessToken (String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:3000/api/login/oauth2/code/kakao");
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    public KakaoProfile findProfile(OauthToken oauthToken) {

        String accessToken = oauthToken.getAccess_token();

        // HTTP 메세지 처리 인터페이스 선언
        RestTemplate rt = new RestTemplate();


        // 헤더정보에 token값과 content-type 설정 (kakao에서 지정한 필수 항목)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 위에서 설정한 헤더정보를 담는 HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        try {
            // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
            ResponseEntity<KakaoProfile> kakaoProfileResponse = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoProfileRequest,
                    KakaoProfile.class
            );

            System.out.println(kakaoProfileResponse.getBody());
            return kakaoProfileResponse.getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN);
        }

    }


    public JwtTokenDto saveUserAndGetToken(OauthToken oauthToken) {

        KakaoProfile profile = findProfile(oauthToken);

        System.out.println(profile);

        UserEntity user = null;

        try {
            user = userRepository.findByUserEmail(profile.getKakao_account().getEmail()).get();
        } catch (NoSuchElementException e) {

            String userName = "이름넣어줄건가요";
            Long userBoardNum = 0L;
            Long userCommentNum = 0L;


            user = UserEntity.builder()
                    .kakao_id(profile.getId())
                    .image(profile.getKakao_account().getProfile().getProfile_image_url())
                    .thumbnail(profile.getKakao_account().getProfile().getThumbnail_image_url())
                    .userNickname(profile.getKakao_account().getProfile().getNickname())
                    .userEmail(profile.getKakao_account().getEmail())
                    .userPassword(null)
                    .myRole(Role.MEMBER)
                    .loginType(LoginType.KAKAO)
                    .userName(userName)
                    .userBoardNum(userBoardNum)
                    .userCommentNum(userCommentNum)
                    .build();

            userRepository.save(user);
        }
        redisService.setValueWithTTL(user.getUserId().toString(), oauthToken, 7L, TimeUnit.DAYS);

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), user.getUserId().toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void serviceLogout(PrincipalDetails principalDetails){
        // 서비스 로그아웃(카카오)
        UserEntity user = principalDetails.getUser();

        if(user.getLoginType()!= LoginType.KAKAO)
            throw new CustomException(ErrorCode.USER_INVALID_LOGIN_TYPE);

        OauthToken oauthToken = getOauthToken(user.getUserId());
        if(oauthToken == null) return;

        System.out.println(oauthToken);

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        HttpEntity<MultiValueMap<String, String>> logoutRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    logoutRequest,
                    String.class
            );

            System.out.println("회원번호 " + response.getBody() + " 로그아웃");
            redisService.deleteValue(user.getUserId().toString());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN);
        }

    }

    public boolean isExpired(Long userId) {

        // OauthToken 정보를 받아와서 만료됐는지 검사
        // 만료되었으면 true, 아직 유효하면 false
        OauthToken oauthToken = (OauthToken) redisService.getValue(userId.toString());
        if (oauthToken==null) return true;

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<OauthTokenInfoRes> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/access_token_info",
                    HttpMethod.GET,
                    request,
                    OauthTokenInfoRes.class
            );
            return false;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && ex.getResponseBodyAsString().contains("-401")) {
                return true;
            } else {
                throw new CustomException(ErrorCode.AUTH_INVALID_KAKAO_CODE);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public OauthToken getRefresh(Long userId) {

        // OauthToken을 refresh하는 메서드.
        OauthToken oauthToken = (OauthToken) redisService.getValue(userId.toString());
        if(oauthToken == null) return null;

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", oauthToken.getRefresh_token());
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> refreshRequest = new HttpEntity<>(params, headers);

        ResponseEntity<OauthToken> refreshResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                refreshRequest,
                OauthToken.class
        );
        OauthToken refreshOauthToken = refreshResponse.getBody();
        refreshOauthToken.setScope(oauthToken.getScope());

        if (refreshOauthToken.getRefresh_token() == null) {
            refreshOauthToken.setRefresh_token(oauthToken.getRefresh_token());
        }
        redisService.setValueWithTTL(userId.toString(), refreshOauthToken, 7L, TimeUnit.DAYS);

        return refreshOauthToken;
    }

    public OauthToken getOauthToken(Long userId) {
        OauthToken oauthToken;
        if (isExpired(userId)) {
            oauthToken = getRefresh(userId);
        }else {
            oauthToken = (OauthToken) redisService.getValue(userId.toString());
        }

        return oauthToken;
    }
}
