package com.turing.forseason.global.errorExeption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomExeption extends RuntimeException{
    public ErrorCode errorCode;
}
