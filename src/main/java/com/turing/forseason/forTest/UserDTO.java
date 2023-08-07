package com.turing.forseason.forTest;

import com.turing.forseason.entity.UserEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
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

    public static UserDTO toUserDTO(UserEntity userEntity){
        UserDTO userDTO = UserDTO.builder()
                .userId(userEntity.getUserId())
                .userName(userEntity.getUserName())
                .userEmail(userEntity.getUserEmail())
                .userPassword(userEntity.getUserPassword())
                .userNickname(userEntity.getUserNickname())
                .userPhoneNumber(userEntity.getUserPhoneNumber())
                .userProfilePicture(userEntity.getUserProfilePicture())
                .build();
        return userDTO;
    }

    public static UserEntity toUserEntity(UserDTO userDTO){
        UserEntity userEntity = UserEntity.builder()
                .userName(userDTO.getUserName())
                .userEmail(userDTO.getUserEmail())
                .userPassword(userDTO.getUserPassword())
                .userNickname(userDTO.getUserNickname())
                .userPhoneNumber(userDTO.getUserPhoneNumber())
                .userProfilePicture(userDTO.getUserProfilePicture())
                .build();
        return userEntity;
    }
}
