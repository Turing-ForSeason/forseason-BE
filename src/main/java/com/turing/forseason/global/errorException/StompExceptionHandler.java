package com.turing.forseason.global.errorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.forseason.global.dto.StompErrorResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {
    public StompExceptionHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        //여기서 JWT 토큰 예외처리도 추가해야 함
        if(ex instanceof StompException){
            // exception을 토대로 errorResponse를 만듬
            StompException stompException = (StompException) ex;
            StompErrorResponse errorResponse = new StompErrorResponse(stompException.errorCode);

            // errorResponse를 전송할 것임.
            return prepareErrorMessage(errorResponse);
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> prepareErrorMessage(StompErrorResponse errorResponse) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // errorResponse를 String으로 전환 (바디 부분에 넣을거임)
            String errorPayload = objectMapper.writeValueAsString(errorResponse);

            // STOMP 메세지 헤더 추가
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
            accessor.setLeaveMutable(true);

            // STOMP 메세지 바디, 헤더 만들어서 전송
            return MessageBuilder.createMessage(errorPayload.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());

        } catch (Throwable e) {
            System.out.println(e);
            return null;
        }
    }
}
