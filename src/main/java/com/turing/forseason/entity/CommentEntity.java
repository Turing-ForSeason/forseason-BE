package com.turing.forseason.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @Column(name = "comment_contents", nullable = false, columnDefinition = "TEXT")
    private String commentContents;

    @Column(name = "comment_date", nullable = false)
    private Date commentDate;

    @Column(name = "comment_like_num", columnDefinition = "INT DEFAULT 0")
    private int commentLikeNum;

    @Column(name = "comment_user_id", nullable = false)
    private int commentUserId;

    @Column(name = "comment_board_id", nullable = false)
    private int commentBoardId;

    @Column(name = "comment_user_nickname", nullable = false, length = 10)
    private String commentUserNickname;

    @Column(name = "comment_user_profile_picture", columnDefinition = "TEXT")
    private String commentUserProfilePicture;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCommentContents() {
        return commentContents;
    }

    public void setCommentContents(String commentContents) {
        this.commentContents = commentContents;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public int getCommentLikeNum() {
        return commentLikeNum;
    }

    public void setCommentLikeNum(int commentLikeNum) {
        this.commentLikeNum = commentLikeNum;
    }

    public int getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(int commentUserId) {
        this.commentUserId = commentUserId;
    }

    public int getCommentBoardId() {
        return commentBoardId;
    }

    public void setCommentBoardId(int commentBoardId) {
        this.commentBoardId = commentBoardId;
    }

    public String getCommentUserNickname() {
        return commentUserNickname;
    }

    public void setCommentUserNickname(String commentUserNickname) {
        this.commentUserNickname = commentUserNickname;
    }

    public String getCommentUserProfilePicture() {
        return commentUserProfilePicture;
    }

    public void setCommentUserProfilePicture(String commentUserProfilePicture) {
        this.commentUserProfilePicture = commentUserProfilePicture;
    }
}