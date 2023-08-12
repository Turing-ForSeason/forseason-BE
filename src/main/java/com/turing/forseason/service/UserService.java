package com.turing.forseason.service;

import com.turing.forseason.entity.UserEntity;
import com.turing.forseason.forTest.MemberJoinRequestDto;
import com.turing.forseason.forTest.MemberLoginRequestDto;
import com.turing.forseason.forTest.MemberLoginResponseDto;
import com.turing.forseason.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    //테스트용으로 잠깐 만듬
    private final UserRepository userRepository;

    public UserEntity login(MemberLoginRequestDto memberLoginRequestDto){
        Optional<UserEntity> user = userRepository.findByUserEmail(memberLoginRequestDto.getEmail());

        if(user.isPresent()){
            if(user.get().getUserPassword().equals(memberLoginRequestDto.getPassword())){
                return user.get();
            }
        }

        return null;
    }

    public UserEntity join(MemberJoinRequestDto memberJoinRequestDto) {
        System.out.println("1");
        UserEntity userEntity = MemberJoinRequestDto.toUserEntity(memberJoinRequestDto);
        System.out.println("UserService.join");
        System.out.println("userEntity = " + userEntity);
        return userRepository.save(userEntity);
    }
}
