package com.turing.forseason.domain.board.entity;

import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.global.entity.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "board")
@SQLDelete(sql = "UPDATE board SET deleted_at = NOW() where board_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
public class BoardEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false, columnDefinition = "bigint")
    private Long boardId;

    @Column(name = "board_title", nullable = false)
    private String boardTitle;

    @Column(name = "board_contents", nullable = false, columnDefinition = "TEXT")
    private String boardContents;

    @Column(name = "board_date", columnDefinition = "timestamp") // 테스트하려고 nullable = false 없애고 밑에 생성자도 추가함
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

    @Column(name = "board_location", nullable = false)
    private String boardLocation;

    @Builder
    public BoardEntity(Long boardId, String boardTitle, String boardContents, String boardPicture, Long boardLikeNum, Long boardCommentNum,
                       String boardUserProfilePicture, String boardUserNickname, UserEntity user, String boardHashtags, String boardLocation, LocalDateTime boardDate) {
        this.boardId = boardId;
        this.boardTitle = boardTitle;
        this.boardContents = boardContents;
        this.boardPicture = boardPicture;
        this.boardLikeNum = boardLikeNum;
        this.boardCommentNum = boardCommentNum;
        this.boardUserProfilePicture = boardUserProfilePicture;
        this.boardUserNickname = boardUserNickname;
        this.user = user;
        this.boardHashtags = boardHashtags;
        this.boardLocation = boardLocation;
        this.boardDate = boardDate;
    }

    public void increaseLikeNum(){
        this.boardLikeNum++;
    }
}
