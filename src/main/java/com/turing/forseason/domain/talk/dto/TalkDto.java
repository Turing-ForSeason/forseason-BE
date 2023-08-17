package com.turing.forseason.domain.talk.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkDto {
    // 해당 DTO는 커뮤니티 페이지 렌더링에서 사용
    private Long talkId;

    private String Contents;

    private String userNickname;

    private String userProfilePicture;

    private String location;

    private LocalDateTime date;

    @Builder
    public TalkDto(Long talkId, String contents, String userNickname, String userProfilePicture, String location, LocalDateTime date) {
        this.talkId = talkId;
        Contents = contents;
        this.userNickname = userNickname;
        this.userProfilePicture = userProfilePicture;
        this.location = location;
        this.date = date;
    }
}
