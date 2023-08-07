package com.turing.forseason.service;

import com.turing.forseason.entity.UserEntity;
import com.turing.forseason.forTest.UserDTO;
import com.turing.forseason.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    //테스트용으로 잠깐 만듬
    private final UserRepository userRepository;

    public UserDTO login(UserDTO userDTO){
        Optional<UserEntity> getByEmail = userRepository.findByUserEmail(userDTO.getUserEmail());

        if(getByEmail.isPresent()){
            // 회원이 있다면
            UserEntity userInfo = getByEmail.get();
            if (userInfo.getUserPassword().equals(userDTO.getUserPassword())) {
                //비밀번호 일치
                // entity -> DTO로 변환 후 리턴
                UserDTO response = UserDTO.toUserDTO(userInfo);
                return response;
            }else {
                //비밀번호 불일치
                System.out.println("비밀번호 불일치");
                return null;
            }
        }else{
            //조회 결과 없음
            System.out.println("조회 결과 없음");
            return null;
        }
    }

    public UserEntity join(UserDTO userDTO) {
        System.out.println("1");
        UserEntity userEntity = UserDTO.toUserEntity(userDTO);
        System.out.println("UserService.join");
        System.out.println("userEntity = " + userEntity);
        return userRepository.save(userEntity);
    }
}
