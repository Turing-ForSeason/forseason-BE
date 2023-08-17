package com.turing.forseason.domain.user.domain;

import com.turing.forseason.global.jwt.OauthToken;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class OauthTokenMap {
    private static OauthTokenMap oauthTokenMap = new OauthTokenMap();

    private HashMap<Long, OauthToken> OauthTokens = new HashMap<>(); // <userId, OauthToken>

    private OauthTokenMap() {}

    public static OauthTokenMap getInstance() {
        return oauthTokenMap;
    }
}
