package com.turing.forseason.global.config.stomp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.global.errorException.StompErrorCode;
import com.turing.forseason.global.errorException.StompException;
import com.turing.forseason.domain.talk.service.TalkService;
import com.turing.forseason.global.jwt.JwtProperties;
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
        // 코드가 많이 더러운데 추후 고칠 예정

        // 헤더 뽑기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.DISCONNECT.equals(headerAccessor.getCommand()))return message;

        System.out.println(message);

        // Jwt 헤더 뽑아내기
        String jwtHeader = headerAccessor.getFirstNativeHeader(JwtProperties.HEADER_STRING);

        // JWT 헤더가 없거나 bearer가 아닐경우
        if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)){
            throw new StompException(StompErrorCode.INVALID_TOKEN);
        }
        else{
            String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

            try {
                // 토큰 유효성 검사
                JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token);
            } catch (TokenExpiredException e) {
                // 토큰 만료시
                throw new StompException(StompErrorCode.INVALID_TOKEN_EXPIRED);
            } catch (JWTVerificationException e) {
                // 토큰 유효하지 않을 시
                throw new StompException(StompErrorCode.INVALID_TOKEN);
            }
        }

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