package com.turing.forseason.domain.user.dto;

import lombok.Getter;

@Getter
public class SignUpRequestDto {
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userNickname;
    private String image;

}
