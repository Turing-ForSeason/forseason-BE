package com.turing.forseason.domain.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.turing.forseason.domain.user.domain.KakaoProfile;
import com.turing.forseason.domain.user.domain.OauthTokenMap;
import com.turing.forseason.domain.user.dto.OauthTokenInfoRes;
import com.turing.forseason.domain.user.entity.Role;
import com.turing.forseason.domain.user.dto.UserDetailDto;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtProperties;
import com.turing.forseason.global.jwt.OauthToken;
import com.turing.forseason.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
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


    public String saveUserAndGetToken(OauthToken oauthToken) {

        KakaoProfile profile = findProfile(oauthToken);

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
        addOauthToken(oauthToken, user.getUserId());

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

    public void kakaoLogout(){
        // 카카오계정 로그아웃
        RestTemplate rt = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/logout")
                .queryParam("client_id", "262778662e9437ec42d6cc9d231e88bc")
                .queryParam("logout_redirect_uri", "http://localhost:8080/logout/service");
        String uri = builder.toUriString();
        try {
            ResponseEntity<String> response = rt.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    String.class
            );
        }catch(HttpClientErrorException ex){
            System.out.println("카카오 로그아웃 실패");
            System.out.println(ex.getResponseBodyAsString());
            throw new CustomException(ErrorCode.AUTH_BAD_LOGOUT_REQUEST);
        }
    }

    public void serviceLogout(PrincipalDetails principalDetails){
        // 서비스 로그아웃
        OauthToken oauthToken = OauthTokenMap.getInstance().getOauthTokens().get(principalDetails.getUser().getUserId());
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
            deleteOauthToken(principalDetails.getUser().getUserId());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN);
        }

    }

    public OauthToken addOauthToken(OauthToken oauthToken, Long userId){
        // OauthTokenMap에 OauthToken 저장
        HashMap<Long, OauthToken> oauthTokens = OauthTokenMap.getInstance().getOauthTokens();
        if(!oauthTokens.containsKey(userId)) {
            oauthTokens.put(userId, oauthToken);
        }
        return oauthToken;
    }

    public void deleteOauthToken(Long userId) {
        // OauthTokenMap에서 삭제
        OauthTokenMap.getInstance().getOauthTokens().remove(userId);
    }

    public OauthToken updateOauthToken(Long userId, OauthToken newOauthToken) {
        // OauthTokenMap에서 업데이트 (refresh할때 사용)
        deleteOauthToken(userId);
        addOauthToken(newOauthToken, userId);
        return newOauthToken;
    }

    public boolean isExpired(OauthToken oauthToken) {

        // OauthToken 정보를 받아와서 만료됐는지 검사
        // 만료되었으면 ture, 아직 유효하면 false
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());

        try {
            ResponseEntity<OauthTokenInfoRes> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/access_token_info",
                    HttpMethod.GET,
                    null,
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

    public OauthToken getRefresh(OauthToken oauthToken) {

        // OauthToken을 refresh하는 메서드.
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "262778662e9437ec42d6cc9d231e88bc");
        params.add("refresh_token", oauthToken.getRefresh_token());
        params.add("client_secret", "vhJNa6nXjI0QFOAxpH2CkTtiOpd42LRb");

        HttpEntity<MultiValueMap<String, String>> refreshRequest = new HttpEntity<>(params, headers);

        ResponseEntity<OauthToken> refreshResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                refreshRequest,
                OauthToken.class
        );
        OauthToken refreshOauthToken = refreshResponse.getBody();
        refreshOauthToken.setScope(oauthToken.getScope());
        return refreshOauthToken;
    }
}