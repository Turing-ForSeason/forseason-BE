package com.turing.forseason.global.dto;

import com.turing.forseason.global.errorException.StompErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StompErrorResponse {
    private int code;
    private String message;

    public StompErrorResponse(StompErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}