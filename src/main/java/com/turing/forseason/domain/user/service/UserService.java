package com.turing.forseason.domain.user.service;



import com.turing.forseason.domain.user.dto.*;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUserById(Long userId) {

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND)
        );

        return user;
    }

    @Transactional
    public UserEntity update(Long userId, UpdateUserInfoRequest request) {

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND)
        );

        user.update(request.getUserName(), request.getUserNickname());

        return user;
    }

    public UserEntity getUser(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        System.out.println(userId+"3");

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND)
        );

        return user;
    }

    public UserDetailDto getUserDetail(UserEntity user) {
        return new UserDetailDto(user);
    }

}