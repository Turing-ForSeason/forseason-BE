package com.turing.forseason.global.jwt;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpiresIn;


    @Builder
    public JwtTokenDto(String accessToken, String refreshToken, Date accessTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
