package com.turing.forseason.global.errorException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.MessageDeliveryException;

@Getter
@Setter
public class StompException extends MessageDeliveryException {
    public StompErrorCode errorCode;

    public StompException(StompErrorCode errorCode) {
        super(errorCode.getMessage());  // Exception의 메시지로 errorCode의 메시지를 전달
        this.errorCode = errorCode;
    }
}
