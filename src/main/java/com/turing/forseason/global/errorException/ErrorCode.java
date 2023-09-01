package com.turing.forseason.global.errorException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 성공 관련(1000번대 코드 사용)
    SUCCESS_OK(HttpStatus.OK, "성공", 1000),
    // 정석대로는 헤더필드의 Location에 URI를 넣어서 반환해야되는데 구현 할까요...?
    SUCCESS_CREATED(HttpStatus.CREATED, "생성 성공", 1001),


    // JWT & Auth 관련(2000번대 코드 사용)
    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.", 2001),
    JWT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", 2002),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다.", 2003),
    JWT_WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰 입니다.", 2004),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 2005),
    AUTH_INVALID_KAKAO_CODE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", 2006),
    AUTH_EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰입니다.", 2007),
    AUTH_BAD_LOGOUT_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다", 2008),




    // Board 관련(3000번대 코드 사용)
    BOARD_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", 3001),


    // User 관련(4000번대 코드 사용)


    // comment 관련(5000번대 코드 사용)


    // Talk 관련(6000번대 코드 사용)
    TALK_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 6001),
    TALK_USER_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 6002),
    TALK_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 채팅방을 찾을 수 없습니다.", 6003),
    TALK_DUPLICATED_USER(HttpStatus.NOT_ACCEPTABLE, "중복된 사용자입니다.", 6004),


    // 원인 불명 에러(7000번대 코드 사용)
    UNKNOWN_ERROR(HttpStatus.BAD_GATEWAY, "알 수 없는 오류입니다",7001),
    ;


    private final HttpStatus httpStatus;
    private final String message;
    private final int code;
}
