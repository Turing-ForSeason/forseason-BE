package com.turing.forseason.domain.talk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StompMessage {
    // 해당 DTO는 소켓 통신에서 사용
    /*
    유저 -> 서버 DTO
     */
    // 메시지  타입 : 입장, 채팅
    // 메시지 타입에 따라서 동작하는 구조가 달라진다.
    // 입장과 퇴장 ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
    // TALK 는 말 그대로 내용이 해당 채팅방을 SUB 하고 있는 모든 클라이언트에게 전달된다.
    public enum MessageType {
        ENTER, TALK, LEAVE, MINE,
    }

    private MessageType type; //메세지 타입
    private String location;
    private String userUUID; // 채팅을 보낸 사람
    private String userNickname;
    private String userProfilePicture;
    private String content; // 메세지
    private LocalDateTime date; //채팅 발송 시간

    public void defineType(MessageType messageType) {
        this.type = messageType;
    }
    public void setDateNow() {
        this.date = LocalDateTime.now();
    }

    @Builder
    public StompMessage(MessageType type, String location, String userUUID, String userNickname,
                        String userProfilePicture, String content, LocalDateTime date) {
        this.type = type;
        this.location = location;
        this.userUUID = userUUID;
        this.userNickname = userNickname;
        this.userProfilePicture = userProfilePicture;
        this.content = content;
        this.date = date;
    }
}