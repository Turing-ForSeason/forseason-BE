package com.turing.forseason.domain.user.dto;

import lombok.Getter;

@Getter
public class SignInRequestDto {
    private String userEmail;
    private String userPassword;
}
