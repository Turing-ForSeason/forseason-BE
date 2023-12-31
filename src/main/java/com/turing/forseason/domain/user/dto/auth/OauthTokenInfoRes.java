package com.turing.forseason.domain.user.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
public class OauthTokenInfoRes {
    private Integer code;
    private String msg;

    private Long id;
    private Integer expires_in;
    private Integer app_id;
}
