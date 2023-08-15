package com.turing.forseason.domain.comment.entity;

import com.turing.forseason.domain.board.entity.BoardEntity;
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
@Table(name = "Comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false ,columnDefinition = "bigint")
    private Long commentId;

    @Column(name = "comment_contents", nullable = false, columnDefinition = "TEXT")
    private String commentContents;

    @Column(name = "comment_date", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime commentDate;

    @Column(name = "comment_like_num", columnDefinition = "bigint default 0")
    private Long commentLikeNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @Column(name = "comment_user_nickname", nullable = false)
    private String commentUserNickname;

    @Column(name = "comment_user_profile_picture", columnDefinition = "TEXT")
    private String commentUserProfilePicture;


}