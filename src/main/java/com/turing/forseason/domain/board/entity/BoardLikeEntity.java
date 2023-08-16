package com.turing.forseason.domain.board.entity;

import com.turing.forseason.domain.comment.entity.CommentEntity;
import com.turing.forseason.domain.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "board_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false ,columnDefinition = "bigint")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private CommentEntity comment;

    @Builder
    public BoardLikeEntity(Long likeId, UserEntity user, CommentEntity comment) {
        this.likeId = likeId;
        this.user = user;
        this.comment = comment;
    }
}
