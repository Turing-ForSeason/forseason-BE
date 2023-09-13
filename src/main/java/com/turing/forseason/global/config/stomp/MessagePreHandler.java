package com.turing.forseason.global.config.stomp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.global.errorException.StompErrorCode;
import com.turing.forseason.global.errorException.StompException;
import com.turing.forseason.domain.talk.service.TalkService;
import com.turing.forseason.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MessagePreHandler implements ChannelInterceptor {
    // 서버가 메세지를 전달 받으면 로직 실행전에 실행되는 클래스.
    private final TalkService talkService;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public MessagePreHandler(TalkService talkService, JwtTokenProvider tokenProvider) {
        this.talkService = talkService;
        this.tokenProvider = tokenProvider;

        this.objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // 헤더 뽑기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        // 메세지 프레임이 DISCONNECT라면, JWT토큰 검사 필요없음. (어차피 연결헤제하려는 메세지이기 때문)
        if(StompCommand.DISCONNECT.equals(headerAccessor.getCommand()))return message;


        // 소켓이 처음 연결될때만 JWT 토큰 검증.
        if(StompCommand.CONNECTED.equals(headerAccessor.getCommand())){
            String jwtHeader = headerAccessor.getFirstNativeHeader(JwtTokenProvider.HEADER_STRING);
            if(jwtHeader == null || !jwtHeader.startsWith(JwtTokenProvider.TOKEN_PREFIX)){
                throw new StompException(StompErrorCode.INVALID_TOKEN);
            }
            else{
                String token = jwtHeader.replace(JwtTokenProvider.TOKEN_PREFIX, "");

                try {
                    // 토큰 유효성 검사
                    tokenProvider.validateToken(token);
                } catch (TokenExpiredException e) {
                    // 토큰 만료시
                    throw new StompException(StompErrorCode.INVALID_TOKEN_EXPIRED);
                } catch (JWTVerificationException e) {
                    // 토큰 유효하지 않을 시
                    throw new StompException(StompErrorCode.INVALID_TOKEN);
                }
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

        return message;
    }
}