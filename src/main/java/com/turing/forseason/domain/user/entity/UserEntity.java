package com.turing.forseason.domain.user.entity;

import com.turing.forseason.global.entity.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "User")
@SQLDelete(sql = "UPDATE User SET deleted_at = NOW() where user_id = ?")
@Where(clause = "deleted_at IS NULL")
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", unique = true, nullable = false)
    private String userEmail;

    @Column(name = "user_nickname", nullable = false)
    private String userNickname;

    @Column(name = "user_board_num", columnDefinition = "bigint default 0")
    private Long userBoardNum;

    @Column(name = "user_comment_num", columnDefinition = "bigint default 0")
    private Long userCommentNum;

    @Column(name = "kakao_id", columnDefinition = "bigint")
    private Long kakao_id;

    @Column(name = "image_url", columnDefinition="TEXT")
    private String image;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnail;

    @Column(nullable = false, unique = true)
    private String nickname;


    @Column(nullable = false, name = "my_role", updatable = false)
    @Enumerated(EnumType.STRING)
    private Role myRole;

    public List<String> getRoleList(){
        if(this.myRole.getValue().length() > 0){
            return Arrays.asList(this.myRole.getValue());
        }
        return new ArrayList<>();
    }
    @Builder
    public UserEntity(Long userId, String userName, String userEmail, String userNickname, Long userBoardNum, Long userCommentNum, Long kakao_id, String image, String thumbnail, String nickname, Role myRole) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userBoardNum = userBoardNum;
        this.userCommentNum = userCommentNum;
        this.kakao_id = kakao_id;
        this.image = image;
        this.thumbnail = thumbnail;
        this.nickname = nickname;
        this.myRole = myRole;
    }

    public void update(String userName, String userNickname, String nickname) {
        //사용자 정보 변경
        this.userName = userName;
        this.userNickname = userNickname;
        this.nickname = nickname;

    }

    //사용자 썸네일/이미지 변경
}