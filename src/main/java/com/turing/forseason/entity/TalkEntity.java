package com.turing.forseason.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Talk")
public class TalkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_id")
    private int talkId;

    @Column(name = "talk_contents", nullable = false, columnDefinition = "TEXT")
    private String talkContents;

    @Column(name = "talk_date", nullable = false)
    private Date talkDate;

    @Column(name = "talk_user_id", nullable = false)
    private int talkUserId;

    @Column(name = "talk_user_nickname", nullable = false, length = 10)
    private String talkUserNickname;

    @Column(name = "talk_user_profile_picture", columnDefinition = "TEXT")
    private String talkUserProfilePicture;

    public int getTalkUserId() {
        return talkUserId;
    }

    public void setTalkUserId(int talkUserId) {
        this.talkUserId = talkUserId;
    }

    public int getTalkId() {
        return talkId;
    }

    public void setTalkId(int talkId) {
        this.talkId = talkId;
    }

    public String getTalkContents() {
        return talkContents;
    }

    public void setTalkContents(String talkContents) {
        this.talkContents = talkContents;
    }

    public Date getTalkDate() {
        return talkDate;
    }

    public void setTalkDate(Date talkDate) {
        this.talkDate = talkDate;
    }

    public String getTalkUserNickname() {
        return talkUserNickname;
    }

    public void setTalkUserNickname(String talkUserNickname) {
        this.talkUserNickname = talkUserNickname;
    }

    public String getTalkUserProfilePicture() {
        return talkUserProfilePicture;
    }

    public void setTalkUserProfilePicture(String talkUserProfilePicture) {
        this.talkUserProfilePicture = talkUserProfilePicture;
    }
}

