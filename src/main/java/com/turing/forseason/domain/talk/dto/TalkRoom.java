package com.turing.forseason.domain.talk.dto;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class TalkRoom {
    private String location; // 채팅방 이름과 같음
    private long userCount = 0; // 채팅방 인원 수
    private HashMap<String, Long> userList = new HashMap<>(); //userUUID, userId

    public static TalkRoom create(String location){
        TalkRoom talkRoom = new TalkRoom();
        talkRoom.location = location;

        return talkRoom;
    }

    public void updateUserCount() {
        this.userCount = this.userList.size();
    }
}