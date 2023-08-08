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


}