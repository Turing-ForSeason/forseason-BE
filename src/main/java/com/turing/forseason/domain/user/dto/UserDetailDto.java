package com.turing.forseason.domain.user.dto;


import com.turing.forseason.domain.user.entity.UserEntity;

// 이거 왜쓴거임?
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
