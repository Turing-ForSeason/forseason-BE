package com.turing.forseason.forTest;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberLoginResponseDto {
    public enum Auth{
        REJECT, CONFIRM;
    }

    private Auth auth;
    private Integer userId;
}
