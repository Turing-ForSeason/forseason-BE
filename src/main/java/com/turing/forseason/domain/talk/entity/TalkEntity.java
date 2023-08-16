package com.turing.forseason.domain.talk.entity;

import com.turing.forseason.global.entity.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Table(name = "Talk")
@SQLDelete(sql = "UPDATE Talk SET deleted_at = NOW() where talk_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
public class TalkEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_id")
    private Long talkId;

    @Column(name = "talk_contents", nullable = false, columnDefinition = "TEXT")
    private String talkContents;


    @Column(name = "talk_user_id", nullable = false)
    private Long talkUserId;

    @Column(name = "talk_user_nickname", nullable = false)
    private String talkUserNickname;

    @Column(name = "talk_user_profile_picture", columnDefinition = "TEXT")
    private String talkUserProfilePicture;

    @Column(name = "talk_location", nullable = false)
    private String talkLocation;

    @Builder
    public TalkEntity(Long talkId, String talkContents, Long talkUserId, String talkUserNickname, String talkUserProfilePicture, String talkLocation) {
        this.talkId = talkId;
        this.talkContents = talkContents;
        this.talkUserId = talkUserId;
        this.talkUserNickname = talkUserNickname;
        this.talkUserProfilePicture = talkUserProfilePicture;
        this.talkLocation = talkLocation;
    }
}

