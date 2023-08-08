package com.turing.forseason.global.errorExeption;

import com.turing.forseason.global.dto.ApplicationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomExeption.class)
    protected ResponseEntity<ApplicationErrorResponse> handleCustomException(CustomExeption e){
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ApplicationErrorResponse(e.getErrorCode()));
    }
}
