package com.turing.forseason.global.errorException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StompErrorCode {
    SUCCESS("전송 성공", 6000),
    INVALID_TOKEN("유효하지 않는 토큰입니다.", 6001),
    INVALID_TOKEN_EXPIRED("토큰이 만료되었습니다.", 6001),
    INVALID_USER_UUID("유효하지 않은 사용자입니다.", 6002),
    INVALID_LOCATION("유효하지 않은 채팅방입니다.", 6003)
    ;

    private final String message;
    private final int code;
}