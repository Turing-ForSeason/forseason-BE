package com.turing.forseason.domain.talk.map;

import com.turing.forseason.domain.talk.dto.TalkRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class TalkRooms {
    private static TalkRooms talkRooms = new TalkRooms(); //싱글톤으로 만들라고.
    private Map<String, TalkRoom> talkRoomMap = new LinkedHashMap<>(); //<location, ChatRoom>
    private TalkRooms() {
        this.talkRoomMap.put("제주도", TalkRoom.create("제주도"));

        this.talkRoomMap.put("경상북도", TalkRoom.create("경상북도"));
        this.talkRoomMap.put("경상남도", TalkRoom.create("경상남도"));

        this.talkRoomMap.put("전라북도", TalkRoom.create("전라북도"));
        this.talkRoomMap.put("전라남도", TalkRoom.create("전라남도"));

        this.talkRoomMap.put("충청북도", TalkRoom.create("충청북도"));
        this.talkRoomMap.put("충청남도", TalkRoom.create("충청남도"));

        this.talkRoomMap.put("강원도", TalkRoom.create("강원도"));

        this.talkRoomMap.put("세종", TalkRoom.create("세종"));
        this.talkRoomMap.put("광주", TalkRoom.create("광주"));
        this.talkRoomMap.put("대전", TalkRoom.create("대전"));
        this.talkRoomMap.put("울산", TalkRoom.create("울산"));
        this.talkRoomMap.put("인천", TalkRoom.create("인천"));
        this.talkRoomMap.put("대구", TalkRoom.create("대구"));
        this.talkRoomMap.put("부산", TalkRoom.create("부산"));

        this.talkRoomMap.put("경기도", TalkRoom.create("경기도"));
        this.talkRoomMap.put("서울", TalkRoom.create("서울"));

    }

    public static TalkRooms getInstance(){ return talkRooms; }

}