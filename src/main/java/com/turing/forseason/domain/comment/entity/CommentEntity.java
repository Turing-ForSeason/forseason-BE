package com.turing.forseason.domain.comment.entity;

import com.turing.forseason.domain.board.entity.BoardEntity;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.global.entity.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Table(name = "comment")
@SQLDelete(sql = "UPDATE comment SET deleted_at = NOW() where comment_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
public class CommentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false ,columnDefinition = "bigint")
    private Long commentId;

    @Column(name = "comment_contents", nullable = false, columnDefinition = "TEXT")
    private String commentContents;

//    @Column(name = "comment_date", nullable = false, columnDefinition = "timestamp")
//    private LocalDateTime commentDate;

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

    @Builder
    public CommentEntity(Long commentId, String commentContents, Long commentLikeNum, UserEntity user, BoardEntity board, String commentUserNickname, String commentUserProfilePicture) {
        this.commentId = commentId;
        this.commentContents = commentContents;
        this.commentLikeNum = commentLikeNum;
        this.user = user;
        this.board = board;
        this.commentUserNickname = commentUserNickname;
        this.commentUserProfilePicture = commentUserProfilePicture;
    }
}