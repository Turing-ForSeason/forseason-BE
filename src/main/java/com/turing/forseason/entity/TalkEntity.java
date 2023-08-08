package com.turing.forseason.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Column(name = "talk_contents", nullable = false, columnDefinition = "TEXT")
    private String talkContents;

    @Column(name = "talk_date", nullable = false)
    private LocalDateTime talkDate;

    @Column(name = "talk_user_id", nullable = false)
    private int talkUserId;

    @Column(name = "talk_user_nickname", nullable = false, length = 10)
    private String talkUserNickname;

    @Column(name = "talk_user_profile_picture", columnDefinition = "TEXT")
    private String talkUserProfilePicture;

    @Column(name = "talk_location", nullable = false, length = 20)
    private String talkLocation;

}

