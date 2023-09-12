package com.turing.forseason.domain.user.dto.auth;

import lombok.Getter;

@Getter
public class EmailVerificationDto {
    private String userEmail;
    private String code;
}
