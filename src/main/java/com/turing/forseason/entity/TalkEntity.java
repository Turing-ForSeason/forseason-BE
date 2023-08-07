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
@Table(name = "Talk")
public class TalkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_id")
    private int talkId;

    @Column(nullable = false)
    private String talkContents;

    @Column(nullable = false)
    private Date talkDate;

    @Column(nullable = false)
    private int talkUserId;

    @Column(nullable = false)
    private String talkUserNickname;

    @Column(nullable = false)
    private String talkUserProfilePicture;


    public void setTalkUserProfilePicture(String talkUserProfilePicture) {
        this.talkUserProfilePicture = talkUserProfilePicture;
    }
}

