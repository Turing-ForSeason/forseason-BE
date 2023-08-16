package com.turing.forseason.domain.talk.service;

import com.turing.forseason.domain.talk.dto.StompMessage;
import com.turing.forseason.domain.talk.dto.TalkRoom;
import com.turing.forseason.domain.talk.entity.TalkEntity;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.errorException.StompErrorCode;
import com.turing.forseason.global.errorException.StompException;
import com.turing.forseason.domain.talk.mapper.TalkRooms;
import com.turing.forseason.domain.talk.repository.TalkRepository;
import com.turing.forseason.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TalkService {
    private Map<String, TalkRoom> talkRoomMap = TalkRooms.getInstance().getTalkRoomMap(); //<location, ChatRoom>
    private final TalkRepository talkRepository;
    private final UserRepository userRepository;


    public List<TalkEntity> getTalks(String location, int page) {
        //최신 100개의 TAlk 불러오기. 아마 뒤집어야할지 모름.
        Pageable pageable = PageRequest.of(page, 100);
        List<TalkEntity> talkList = talkRepository.findByTalkLocationOrderByTalkDateDesc(location, pageable);
        Collections.reverse(talkList);
        return talkList;
    }

    public List<StompMessage> talk2Messages(String location, String userUUID, List<TalkEntity> talkList) {
        //talkEntity->StompMessage 로 바꾸기.
        Long userId = getUserId(location, userUUID);
        List<StompMessage> stompMessageList = new ArrayList<>();

        for (TalkEntity item : talkList) {
            StompMessage stompMessage = StompMessage.builder()
                    .location(location)
                    .content(item.getTalkContents())
                    .userNickname(item.getTalkUserNickname())
                    .userProfilePicture(item.getTalkUserProfilePicture())
                    .date(item.getTalkDate())
                    .userUUID(null)
                    .build();

            // 클라이언트에서 MINE은 무조건 내 채팅으로, TALK은 상대방 채팅으로 뜨게 하기 위함 (getTalks 한정)
            if (item.getTalkUserId() == userId) {
                stompMessage.setType(StompMessage.MessageType.MINE);
            }else{
                stompMessage.setType(StompMessage.MessageType.TALK);
            }
            stompMessageList.add(stompMessage);
        }
        return stompMessageList;
    }


    public TalkEntity storeTalkEntity(StompMessage stompMessage){
        Long userId = getUserId(stompMessage.getLocation(), stompMessage.getUserUUID());

        //DB에 저장
        TalkEntity talkEntity = TalkEntity.builder()
                .talkUserId(userId)
                .talkContents(stompMessage.getContent())
                .talkDate(stompMessage.getDate())
                .talkUserNickname(stompMessage.getUserNickname())
                .talkUserProfilePicture(stompMessage.getUserProfilePicture())
                .talkLocation(stompMessage.getLocation())
                .build();
        return talkRepository.save(talkEntity);
    }

    public void verifyStompMessage(StompMessage stompMessage){
        //stompMessage의 필수 구성요소인 location과 userUUID가 유효한지 검사
        String location = stompMessage.getLocation();
        String userUUID = stompMessage.getUserUUID();

        if(location == null) throw new StompException(StompErrorCode.INVALID_LOCATION);
        if(userUUID == null) throw new StompException(StompErrorCode.INVALID_USER_UUID);

        TalkRoom talkRoom = findByLocation(location);
        Long userId = getUserId(location, userUUID);

        if(talkRoom == null) throw new StompException(StompErrorCode.INVALID_LOCATION);
        if(userId == null) throw new StompException(StompErrorCode.INVALID_USER_UUID);
    }

    public UserEntity getUser(String location, String userUUID) {
        Long userId = getUserId(location, userUUID);
        Optional<UserEntity> user = userRepository.findById(userId);

        if(user.isEmpty()) throw new CustomException(ErrorCode.TALK_USER_ENTITY_NOT_FOUND);

        return user.get();
    }

    public List<TalkRoom> findAllRoom(){
        List chatRooms = new ArrayList(talkRoomMap.values());
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    public TalkRoom findByLocation(String location){
        //채팅방 이름으로 찾기
        System.out.println("findByLocation, location: " + location);
        TalkRoom talkRoom = talkRoomMap.get(location);
        if(talkRoom==null) throw new CustomException(ErrorCode.TALK_ROOM_NOT_FOUND);

        return talkRoom;
    }

    public String addUser(String location, String userUUID, Long userId) {
        // talkRoom의 userList에 사용자 추가
        TalkRoom talkRoom = findByLocation(location);

        if(talkRoom.getUserList().containsValue(userId)){
            throw new CustomException(ErrorCode.TALK_DUPLICATED_USER);
        }

        talkRoom.getUserList().put(userUUID, userId);
        talkRoom.setUserCount(talkRoom.getUserList().size());

        return userUUID;
    }

    public void delUser(String location, String userUUID){
        //채팅방 유저 리스트에서 삭제
        TalkRoom talkRoom = findByLocation(location);
        talkRoom.getUserList().remove(userUUID);
        talkRoom.setUserCount(talkRoom.getUserList().size());
    }

    public Long getUserId(String location, String userUUID){
        //채팅방에서 userId 조회
        TalkRoom talkRoom = findByLocation(location);
        Long userID = talkRoom.getUserList().get(userUUID);
        if(userID==null) throw new CustomException(ErrorCode.TALK_USER_NOT_FOUND);

        return userID;
    }

}
