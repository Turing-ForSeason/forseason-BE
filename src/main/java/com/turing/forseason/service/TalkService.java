package com.turing.forseason.service;

import com.turing.forseason.dto.Message;
import com.turing.forseason.dto.TalkRoom;
import com.turing.forseason.entity.TalkEntity;
import com.turing.forseason.entity.UserEntity;
import com.turing.forseason.global.errorExeption.CustomExeption;
import com.turing.forseason.global.errorExeption.ErrorCode;
import com.turing.forseason.mapper.TalkRooms;
import com.turing.forseason.repository.TalkRepository;
import com.turing.forseason.repository.UserRepository;
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

    public List<Message> talk2Messages(String location, String userUUID, List<TalkEntity> talkList) {
        //talk->message 로 바꾸기.
        int userId = getUserId(location, userUUID);
        List<Message> messageList = new ArrayList<>();

        for (TalkEntity item : talkList) {
            Message message= Message.builder()
                    .location(location)
                    .content(item.getTalkContents())
                    .userNickname(item.getTalkUserNickname())
                    .userProfilePicture(item.getTalkUserProfilePicture())
                    .date(item.getTalkDate())
                    .build();
            if (item.getTalkUserId() == userId) {
                message.setType(Message.MessageType.MINE);
            }else{
                message.setType(Message.MessageType.TALK);
            }
            messageList.add(message);
        }
        return messageList;
    }

//    public MessageDTO initUser(MessageDTO messageDTO){
//        //사용자를 초기화하는 메세지 만들기.
//        int userId = getUserId(messageDTO.getLocation(), messageDTO.getUserUUID());
//        UserEntity user = userRepository.findById(userId).get();
//
//        MessageDTO initMessage = MessageDTO.builder()
//                .type(MessageDTO.MessageType.INIT)
//                .userNickname(user.getUserNickname())
//                .userProfilePicture(user.getUserProfilePicture())
//                .build();
//        return initMessage;
//    }

    public TalkEntity storeTalkEntity(Message message){
        int userId = getUserId(message.getLocation(), message.getUserUUID());
        //DB에 저장
        TalkEntity talkEntity = TalkEntity.builder()
                .talkUserId(userId)
                .talkContents(message.getContent())
                .talkDate(message.getDate())
                .talkUserNickname(message.getUserNickname())
                .talkUserProfilePicture(message.getUserProfilePicture())
                .talkLocation(message.getLocation())
                .build();
        return talkRepository.save(talkEntity);
    }

    public UserEntity getUser(String location, String userUUID) {
        int userId = getUserId(location, userUUID);
        Optional<UserEntity> user = userRepository.findById(userId);

        if(user.isEmpty()) throw new CustomExeption(ErrorCode.TALK_USER_ENTITY_NOT_FOUND);

        return user.get();
    }

    public List<TalkRoom> findAllRoom(){
        List chatRooms = new ArrayList(talkRoomMap.values());
        Collections.reverse(chatRooms); // 채팅방 생성 순서를 최근순으로.(아마 나중에 로직 바꿀듯)

        return chatRooms;
    }

    public TalkRoom findByLocation(String location){
        //채팅방 이름으로 찾기
        TalkRoom talkRoom = talkRoomMap.get(location);
        if(talkRoom==null) throw new CustomExeption(ErrorCode.TALK_ROOM_NOT_FOUND);

        return talkRoom;
    }

    public String addUser(String location, String userUUID, int userId) {
        TalkRoom talkRoom = talkRoomMap.get(location);
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

    public int getUserId(String location, String userUUID){
        //채팅방에서 userId 조회
        TalkRoom talkRoom = findByLocation(location);
        Integer userID = talkRoom.getUserList().get(userUUID);
        if(userID==null) throw new CustomExeption(ErrorCode.TALK_USER_NOT_FOUND);

        return userID;
    }

//    public ArrayList<Integer> getUserList(String location){
//        //채팅방 전체 userList 조회
//        ArrayList<String> list = new ArrayList<>();
//
//        ChatRoom chatRoom = talkRoomMap.get(location);
//
//        // hashmap 을 for 문을 돌린 후
//        // value 값만 뽑아내서 list 에 저장 후 reutrn
//        chatRoom.getUserList().forEach((key, value) -> list.add(value));
//        return list;
//    }
}
