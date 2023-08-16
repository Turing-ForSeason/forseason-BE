package com.turing.forseason.domain.comment.entity;

import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.global.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "comment_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false, columnDefinition = "bigint")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @Builder
    public CommentLikeEntity(Long likeId, UserEntity user, CommentEntity comment) {
        this.likeId = likeId;
        this.user = user;
        this.comment = comment;
    }
}
