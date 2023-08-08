package com.turing.forseason.global.errorExeption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다.", 1001),

    TALK_SUCCESS(HttpStatus.OK, "성공", 5000),

    // 채팅방 userList에 {userUUID, userID}로 매핑된 정보가 없음
    TALK_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 5001),

    // user의 DB 데이터에 접근할 수 없음
    TALK_USER_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 5002),

    // 잘못된 location
    TALK_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 채팅방을 찾을 수 없습니다.", 5003),
    ;


    private final HttpStatus httpStatus;
    private final String message;
    private final int code;
}
