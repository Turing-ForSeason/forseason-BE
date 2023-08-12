package com.turing.forseason.forTest;

import com.turing.forseason.entity.UserEntity;
import com.turing.forseason.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {
    private final UserService userService;

    @PostMapping("/login")
    public MemberLoginResponseDto login(@RequestBody final MemberLoginRequestDto memberLoginRequestDto){
        UserEntity userEntity = userService.login(memberLoginRequestDto);

        if(userEntity==null){
            return MemberLoginResponseDto.builder()
                    .auth(MemberLoginResponseDto.Auth.REJECT)
                    .userId(null)
                    .build();
        } else{
            System.out.println(userEntity.getUserId());
            return MemberLoginResponseDto.builder()
                    .auth(MemberLoginResponseDto.Auth.CONFIRM)
                    .userId(userEntity.getUserId())
                    .build();
        }
    }

    @PostMapping("/join")
    public void join(@RequestBody final MemberJoinRequestDto memberJoinRequestDto) {
        userService.join(memberJoinRequestDto);
    }
}
