package com.turing.forseason.domain.user.domain;

import lombok.Data;

@Data
public class KakaoProfile {

    public Long id;     // 회원 번호
    public String connected_at;     // 서비스에 연결 완료된 시각
    public Properties properties;   // 사용자 Property
    public KakaoAccount kakao_account;  // 카카오 계정 정보

    @Data
    public class Properties {   // 해당 필드는 Deprecated 됨
        public String nickname;
        public String profile_image; // 이미지 경로 필드1
        public String thumbnail_image;
    }

    @Data
    public class KakaoAccount {     // 주석으로 ???된 부분들은 Deprecated 됨
        public Boolean profile_nickname_needs_agreement;    // 사용자 동의 시 닉네임 제공 가능
        public Boolean profile_image_needs_agreement;   // 사용자 동의시 프로필 사진 제공 가능
        public Profile profile; // 프로필 정보
        public Boolean has_email;   // ???
        public Boolean email_needs_agreement;   // 사용자 동의 시 카카오계정 대표 이메일 제공 가능
        public Boolean is_email_valid;  // 이메일 유효 여부
        public Boolean is_email_verified;   // 이메일 인증 여부
        public String email;    // 카카오계정 대표 이메일
        public String has_age_range;    // ???
        public Boolean age_range_needs_agreement;   // 사용자 동의 시 연령대 제공 가능
        public String age_range;    // 연령대 => ex) 20~29
        public Boolean has_birthday;    // ???
        public Boolean birthday_needs_agreement;    // 사용자 동의 시 생일 제공 가능
        public String birthday; // 생일(MMDD 형식)
        public String birthday_type;    // 생일 타입, SOLAR(양력) 또는 LUNAR(음력)
        public Boolean has_gender;  // ???
        public Boolean gender_needs_agreement;  // 사용자 동의 시 성별 제공 가능
        public String gender;   // 성별, female: 여성 & male: 남성


        @Data
        public class Profile {
            public String nickname; // 닉네임
            public String thumbnail_image_url;  // 프로필 미리보기 이미지 URL
            public String profile_image_url; // 이미지 경로 필드2
            public Boolean is_default_image;    // 프로필 사진 URL이 기본 프로필 사진 URL인지 여부
        }
    }
}
