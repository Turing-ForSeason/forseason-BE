package com.turing.forseason.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @Column(nullable = false)
    private String commentContents;

    @Column(nullable = false)
    private Date commentDate;

    @Column(nullable = false)
    private int commentLikeNum;

    @Column(nullable = false)
    private int commentUserId;

    @Column(nullable = false)
    private int commentBoardId;

    @Column(nullable = false)
    private String commentUserNickname;

    @Column(nullable = false)
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