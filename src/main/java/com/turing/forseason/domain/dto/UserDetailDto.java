package com.turing.forseason.domain.dto;


import com.turing.forseason.entity.UserEntity;

public class UserDetailDto {


    private Long userId;

    private String UserName;

    //Oauth login
    private Long kakao_id;
    private String image;
    private String nickname;

    private String UserEmail;

    public UserDetailDto(UserEntity user) {


    }
}
