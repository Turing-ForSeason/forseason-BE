package com.turing.forseason.domain.user.service;

import com.turing.forseason.domain.user.dto.EmailVerificationDto;
import com.turing.forseason.domain.user.dto.SignInRequestDto;
import com.turing.forseason.domain.user.dto.SignUpRequestDto;
import com.turing.forseason.domain.user.entity.LoginType;
import com.turing.forseason.domain.user.entity.Role;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtTokenDto;
import com.turing.forseason.global.jwt.JwtTokenProvider;
import com.turing.forseason.global.mail.MailService;
import com.turing.forseason.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneralAuthService {
    // 일반 로그인 인증 관련 로직
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final MailService mailService;

    public void signUpUser(SignUpRequestDto requestDto) {
        String state = (String) redisService.getValue(requestDto.getUserEmail());
        if(!"verified".equals(state)) throw new CustomException(ErrorCode.USER_EMAIL_AUTHENTICATION_STATUS_EXPIRED);

        UserEntity user = UserEntity.builder()
                .userBoardNum(0L)
                .userCommentNum(0L)
                .userEmail(requestDto.getUserEmail())
                .userPassword(requestDto.getUserPassword())
                .userNickname(requestDto.getUserNickname())
                .userName(requestDto.getUserName())
                .image(requestDto.getImage())
                .thumbnail(requestDto.getImage())
                .kakao_id(null)
                .myRole(Role.MEMBER)
                .loginType(LoginType.GENERAL)
                .build();

        userRepository.save(user);
    }

    public void verifyEmail(EmailVerificationDto emailVerificationDto) {
        // 인증 코드 검사
        String authCode = (String) redisService.getValue(emailVerificationDto.getUserEmail());

        if(!emailVerificationDto.getCode().equals(authCode))
            throw new CustomException(ErrorCode.USER_INVALID_EMAIL_AUTH_CODE);

        redisService.setValueWithTTL(emailVerificationDto.getUserEmail(), "verified", 30, TimeUnit.MINUTES);
    }

    public boolean isDuplicatedEmail(String email) {
        // 이메일 중복 검사 메서드
        if(userRepository.existsByUserEmail(email))
            throw new CustomException(ErrorCode.USER_DUPLICATED_USER_EMAIL);
        return true;
    }
    @Async
    public void sendEmailAuthCode(String email) {
        // 해당 메서드는 메일 전송후, 잘 전송됐는지 검사까지 하므로 매우 처리시간이 김.
        // 따라서 스레드 비동기 처리로 서버 처리 속도를 향상시킴.
        // 이메일 인증 코드 전송하기

        String authCode = mailService.generateCode();
        System.out.println(authCode);

        String body = "";
        body += "<h3>" + "Forseason 이메일 인증 코드입니다." + "</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>" + "인증 코드는 30분간 유효합니다." + "</h3>";

        try {
            mailService.sendEmail(email, "[Forseason] 이메일 인증 코드", body);
        } catch (Throwable e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }

        redisService.setValueWithTTL(email, authCode, 7L, TimeUnit.DAYS);
    }


    public JwtTokenDto signInAndGetToken(SignInRequestDto requestDto) {
        // Email, PW 검증 후 JWT 토큰 발급 & refresh 토큰 redis에 저장.
        UserEntity user = userRepository.findByUserEmail(requestDto.getUserEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_EMAIL));

        if(!user.getUserPassword().equals(requestDto.getUserPassword()))
            throw new CustomException(ErrorCode.USER_INVALID_PASSWORD);

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), user.getUserId().toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }
}
