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

    @Column(nullable = false)
    private String boardTitle;

    @Column(nullable = false)
    private String boardContents;

    @Column(nullable = false)
    private Date boardDate;

    @Column(nullable = false)
    private String boardPicture;

    @Column(nullable = false)
    private int boardLikeNum;

    @Column(nullable = false)
    private int boardCommentNum;

    @Column(nullable = false)
    private String boardUserProfilePicture;

    @Column(nullable = false)
    private String boardUserNickname;

    @Column(nullable = false)
    private int boardUserId;

    @Column(nullable = false)
    private String boardHashtags;

    @Column(nullable = false)
    private String boardLocation;


}
