package com.turing.forseason.global.dto;

import com.turing.forseason.global.errorException.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse<T> {
    private int status;
    private String message;
    private int code;
    private T result;


    public static <T> ApplicationResponse<T> ok(ErrorCode errorCode, T result) {
        ApplicationResponse<T> applicationResponse = new ApplicationResponse<>();

        applicationResponse.setStatus(errorCode.getHttpStatus().value());
        applicationResponse.setMessage(errorCode.getMessage());
        applicationResponse.setCode(errorCode.getCode());
        applicationResponse.setResult(result);

        return applicationResponse;
    }
}
