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


}
