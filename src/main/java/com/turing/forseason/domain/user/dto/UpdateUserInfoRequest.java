package com.turing.forseason.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserInfoRequest {

    private String userName;
    private String userNickname;
    private String nickname;

}
