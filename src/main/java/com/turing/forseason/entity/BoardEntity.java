package com.turing.forseason.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Board")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private int boardId;

    @Column(name = "board_title", nullable = false, length = 50)
    private String boardTitle;

    @Column(name = "board_contents", nullable = false, columnDefinition = "TEXT")
    private String boardContents;

    @Column(name = "board_date", nullable = false)
    private Date boardDate;

    @Column(name = "board_picture", nullable = false, columnDefinition = "TEXT")
    private String boardPicture;

    @Column(name = "board_like_num", columnDefinition = "INT DEFAULT 0")
    private int boardLikeNum;

    @Column(name = "board_comment_num", columnDefinition = "INT DEFAULT 0")
    private int boardCommentNum;

    @Column(name = "board_user_profile_picture", columnDefinition = "TEXT")
    private String boardUserProfilePicture;

    @Column(name = "board_user_nickname", nullable = false, length = 10)
    private String boardUserNickname;

    @Column(name = "board_user_id", nullable = false)
    private int boardUserId;

    @Column(name = "board_hashtags", nullable = false, length = 255)
    private String boardHashtags;

    @Column(name = "board_location", length = 20)
    private String boardLocation;

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getBoardContents() {
        return boardContents;
    }

    public void setBoardContents(String boardContents) {
        this.boardContents = boardContents;
    }

    public Date getBoardDate() {
        return boardDate;
    }

    public void setBoardDate(Date boardDate) {
        this.boardDate = boardDate;
    }

    public String getBoardPicture() {
        return boardPicture;
    }

    public void setBoardPicture(String boardPicture) {
        this.boardPicture = boardPicture;
    }

    public int getBoardLikeNum() {
        return boardLikeNum;
    }

    public void setBoardLikeNum(int boardLikeNum) {
        this.boardLikeNum = boardLikeNum;
    }

    public int getBoardCommentNum() {
        return boardCommentNum;
    }

    public void setBoardCommentNum(int boardCommentNum) {
        this.boardCommentNum = boardCommentNum;
    }

    public String getBoardUserProfilePicture() {
        return boardUserProfilePicture;
    }

    public void setBoardUserProfilePicture(String boardUserProfilePicture) {
        this.boardUserProfilePicture = boardUserProfilePicture;
    }

    public String getBoardUserNickname() {
        return boardUserNickname;
    }

    public void setBoardUserNickname(String boardUserNickname) {
        this.boardUserNickname = boardUserNickname;
    }

    public int getBoardUserId() {
        return boardUserId;
    }

    public void setBoardUserId(int boardUserId) {
        this.boardUserId = boardUserId;
    }

    public String getBoardHashtags() {
        return boardHashtags;
    }

    public void setBoardHashtags(String boardHashtags) {
        this.boardHashtags = boardHashtags;
    }

    public String getBoardLocation() {
        return boardLocation;
    }

    public void setBoardLocation(String boardLocation) {
        this.boardLocation = boardLocation;
    }

}
