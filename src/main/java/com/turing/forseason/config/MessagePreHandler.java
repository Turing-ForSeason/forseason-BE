package com.turing.forseason.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.forseason.dto.StompMessage;
import com.turing.forseason.global.errorException.StompException;
import com.turing.forseason.service.TalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MessagePreHandler implements ChannelInterceptor {
    private final TalkService talkService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 헤더 뽑기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        // 메세지 프레임이 SEND 라면? -> location과 userUUID가 정확한지 확인
        if(StompCommand.SEND.equals(headerAccessor.getCommand())){
            // message에서 Body 뽑아내기
            byte[] payload = (byte[]) message.getPayload();
            String messageBody = new String(payload, StandardCharsets.UTF_8);
            StompMessage stompMessage;

            try{
                // body부분을 StompMessage로 변환
                stompMessage = objectMapper.readValue(messageBody, StompMessage.class);
            }catch(Exception e){
                // 변환 불가 -> 그냥 return
                return message;
            }
            try {
                // location & userUUID 유효성 검사.
                talkService.verifyStompMessage(stompMessage);
            }catch (StompException e){
                throw e;
            }
        }

        // JWT 토큰 유효성 검사 추가 예정

        return message;
    }
}