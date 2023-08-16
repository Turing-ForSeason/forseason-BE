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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=262778662e9437ec42d6cc9d231e88bc");
            sb.append("&redirect_uri=http://localhost:3000/api/login/oauth2/code/kakao");
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();


            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.AUTH_INVALID_KAKAO_CODE);
        }

        return access_Token;
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

        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        System.out.println(kakaoProfileResponse.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            // Json -> KakaoProfile으로 Parse
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            // parsing 실패 시 예외 처리
            e.printStackTrace();
        }

        return kakaoProfile;
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
                    .userId(userId)
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