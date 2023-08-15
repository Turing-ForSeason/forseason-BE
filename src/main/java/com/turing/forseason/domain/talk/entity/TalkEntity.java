package com.turing.forseason.domain.talk.entity;

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
    private Long talkId;

    @Column(name = "talk_contents", nullable = false, columnDefinition = "TEXT")
    private String talkContents;

    @Column(name = "talk_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime talkDate;

    @Column(name = "talk_user_id", nullable = false)
    private Long talkUserId;

    @Column(name = "talk_user_nickname", nullable = false)
    private String talkUserNickname;

    @Column(name = "talk_user_profile_picture", columnDefinition = "TEXT")
    private String talkUserProfilePicture;

    @Column(name = "talk_location", nullable = false)
    private String talkLocation;

}

