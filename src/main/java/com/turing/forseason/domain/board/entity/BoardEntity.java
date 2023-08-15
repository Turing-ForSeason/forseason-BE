package com.turing.forseason.domain.board.entity;

import com.turing.forseason.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    @Column(name = "board_id", nullable = false, columnDefinition = "bigint")
    private Long boardId;

    @Column(name = "board_title")
    private String boardTitle;

    @Column(name = "board_contents", nullable = false, columnDefinition = "TEXT")
    private String boardContents;

    @Column(name = "board_date", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime boardDate;

    @Column(name = "board_picture", nullable = false, columnDefinition = "TEXT")
    private String boardPicture;

    @Column(name = "board_like_num", columnDefinition = "bigint default 0")
    private Long boardLikeNum;

    @Column(name = "board_comment_num", columnDefinition = "bigint default 0")
    private Long boardCommentNum;

    @Column(name = "board_user_profile_picture", columnDefinition = "TEXT")
    private String boardUserProfilePicture;

    @Column(name = "board_user_nickname", nullable = false)
    private String boardUserNickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "board_hashtags", nullable = false)
    private String boardHashtags;

    @Column(name = "board_location")
    private String boardLocation;


}
