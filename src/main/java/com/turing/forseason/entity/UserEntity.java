package com.turing.forseason.entity;

import com.turing.forseason.domain.OauthEnums.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userEmail;


    @Column(nullable = false)
    private String userNickname;

    @Column(nullable = false)
    private Long userBoardNum;

    @Column(nullable = false)
    private Long userCommentNum;

    //Oauth login

    @Column(name = "kakao_id")
    private Long kakao_id;

    @Column(name = "image_url")
    private String image;

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