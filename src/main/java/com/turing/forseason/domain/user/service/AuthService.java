package com.turing.forseason.domain.user.service;

import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.JwtTokenDto;
import com.turing.forseason.global.jwt.JwtTokenProvider;
import com.turing.forseason.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public JwtTokenDto reissue(String oldRefreshToken) {
        String value = (String) redisService.getValue(oldRefreshToken);
        System.out.println(oldRefreshToken);
        System.out.println(value);
        if ("Deprecated".equals(value) || !tokenProvider.validateToken(oldRefreshToken)) {
            // refresh 토큰이 블랙리스트에 존재하는지 검사 & 유효성 검사
            throw new CustomException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(value);
        UserEntity user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(oldRefreshToken, "Deprecated", 7L, TimeUnit.DAYS);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), userId.toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void deprecateTokens(JwtTokenDto jwtTokenDto) {
        String accessToken = jwtTokenDto.getAccessToken().replace(JwtTokenProvider.TOKEN_PREFIX, "");
        String refreshToken = jwtTokenDto.getRefreshToken();

        redisService.setValueWithTTL(accessToken, "Deprecated", 30L, TimeUnit.MINUTES);
        redisService.setValueWithTTL(refreshToken, "Deprecated", 7L, TimeUnit.DAYS);
    }
}
