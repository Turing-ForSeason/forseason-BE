package com.turing.forseason.forTest;

import com.turing.forseason.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberJoinRequestDto {
    /*
    테스트용 클래스임
     */
    private int userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userNickname;
    private String userPhoneNumber;
    private String userProfilePicture;
    private int userBoardNum;
    private int userCommentNum;

    public static MemberJoinRequestDto toUserDTO(UserEntity userEntity){
        MemberJoinRequestDto memberJoinRequestDto = MemberJoinRequestDto.builder()
                .userId(userEntity.getUserId())
                .userName(userEntity.getUserName())
                .userEmail(userEntity.getUserEmail())
                .userPassword(userEntity.getUserPassword())
                .userNickname(userEntity.getUserNickname())
                .userPhoneNumber(userEntity.getUserPhoneNumber())
                .userProfilePicture(userEntity.getUserProfilePicture())
                .build();
        return memberJoinRequestDto;
    }

    public static UserEntity toUserEntity(MemberJoinRequestDto memberJoinRequestDto){
        UserEntity userEntity = UserEntity.builder()
                .userName(memberJoinRequestDto.getUserName())
                .userEmail(memberJoinRequestDto.getUserEmail())
                .userPassword(memberJoinRequestDto.getUserPassword())
                .userNickname(memberJoinRequestDto.getUserNickname())
                .userPhoneNumber(memberJoinRequestDto.getUserPhoneNumber())
                .userProfilePicture(memberJoinRequestDto.getUserProfilePicture())
                .build();
        return userEntity;
    }
}
