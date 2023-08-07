package com.turing.forseason;

import com.turing.forseason.dto.TalkRoom;
import com.turing.forseason.mapper.TalkRooms;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class test {
    @Test
    public void t(){
        Map<String, TalkRoom> roomMap = TalkRooms.getInstance().getTalkRoomMap();
        Assertions.assertThat(roomMap.size()).isEqualTo(2);
    }
}