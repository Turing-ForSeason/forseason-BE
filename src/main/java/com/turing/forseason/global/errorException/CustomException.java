package com.turing.forseason.global.errorException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    public ErrorCode errorCode;
}
