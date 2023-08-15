package com.turing.forseason.global.dto;

import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.global.errorException.StompErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StompResponse {
    private int code;
    private String message;
    private StompMessage result;

    public static StompResponse ok(StompErrorCode errorCode, StompMessage result) {
        StompResponse stompResponse = new StompResponse();

        stompResponse.setCode(errorCode.getCode());
        stompResponse.setMessage(errorCode.getMessage());
        stompResponse.setResult(result);

        return stompResponse;
    }
}
