package com.turing.forseason.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_name", nullable = false, length = 10)
    private String userName;

    @Column(name = "user_email", unique = true, nullable = false, length = 50)
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 30)
    private String userPassword;

    @Column(name = "user_nickname", nullable = false, length = 10)
    private String userNickname;

    @Column(name = "user_phone_number", nullable = false, length = 15)
    private String userPhoneNumber;

    @Column(name = "user_profile_picture", nullable = true, columnDefinition = "TEXT")
    private String userProfilePicture;

    @Column(name = "user_board_num", columnDefinition = "INT DEFAULT 0")
    private int userBoardNum;

    @Column(name = "user_comment_num", columnDefinition = "INT DEFAULT 0")
    private int userCommentNum;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public int getUserBoardNum() {
        return userBoardNum;
    }

    public void setUserBoardNum(int userBoardNum) {
        this.userBoardNum = userBoardNum;
    }

    public int getUserCommentNum() {
        return userCommentNum;
    }

    public void setUserCommentNum(int userCommentNum) {
        this.userCommentNum = userCommentNum;
    }
}