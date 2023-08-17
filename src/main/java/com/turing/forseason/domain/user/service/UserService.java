package com.turing.forseason.domain.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.turing.forseason.domain.user.domain.KakaoProfile;
import com.turing.forseason.domain.user.entity.Role;
import com.turing.forseason.domain.user.dto.UserDetailDto;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtProperties;
import com.turing.forseason.global.jwt.OauthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;


    public OauthToken getKakaoAccessToken (String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "262778662e9437ec42d6cc9d231e88bc");
        params.add("redirect_uri", "http://localhost:3000/api/login/oauth2/code/kakao");
        params.add("code", code);
        params.add("client_secret", "vhJNa6nXjI0QFOAxpH2CkTtiOpd42LRb");

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        try {
            ResponseEntity<OauthToken> accessTokenResponse = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    OauthToken.class
            );

            return accessTokenResponse.getBody();

        }  catch (Exception e){
            throw new CustomException(ErrorCode.AUTH_INVALID_KAKAO_CODE);
        }
    }

    public KakaoProfile findProfile(String token) {

        // HTTP 메세지 처리 인터페이스 선언
        RestTemplate rt = new RestTemplate();

        // 헤더정보에 token값과 content-type 설정 (kakao에서 지정한 필수 항목)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
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


    public String saveUserAndGetToken(String token) {

        KakaoProfile profile = findProfile(token);

        System.out.println(profile);

        UserEntity user = null;

        try {
            user = userRepository.findByUserEmail(profile.getKakao_account().getEmail()).get();
        } catch (NoSuchElementException e) {

            Long userId = 1L;
            String userName = "박민재";
            Long userBoardNum = 1L;
            Long userCommentNum = 1L;


            // 여기서 의문... userName을 어떻게 받아올 생각인지?
            user = UserEntity.builder()
                    .kakao_id(profile.getId())
                    .image(profile.getKakao_account().getProfile().getProfile_image_url())
                    .thumbnail(profile.getKakao_account().getProfile().getThumbnail_image_url())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .userNickname(profile.getKakao_account().getProfile().getNickname())
                    .userEmail(profile.getKakao_account().getEmail())
                    .myRole(Role.MEMBER)
//                    .userId(userId)
                    .userName(userName)
                    .userBoardNum(userBoardNum)
                    .userCommentNum(userCommentNum)
                    .build();

            userRepository.save(user);
        }

        return createToken(user);
    }

    public String createToken(UserEntity user) {


        String jwtToken = JWT.create()

                .withSubject(user.getUserEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))

                .withClaim("id", user.getUserId())
                .withClaim("nickname", user.getUserName())

                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        System.out.println(JwtProperties.SECRET);

        return jwtToken;
    }
    public UserEntity getUser(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        System.out.println(userId+"3");

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND)
        );

        return user;
    }

    public UserDetailDto getUserDetail(UserEntity user) {
        return new UserDetailDto(user);
    }




}