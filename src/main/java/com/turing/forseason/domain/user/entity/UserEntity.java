package com.turing.forseason.domain.user.entity;

import com.turing.forseason.domain.user.domain.OauthEnums.Role;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User")
public class UserEntity {

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

    @Column(name = "humbnail_url", columnDefinition = "TEXT")
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

}