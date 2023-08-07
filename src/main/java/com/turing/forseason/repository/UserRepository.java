package com.turing.forseason.repository;

import com.turing.forseason.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserEmail(String email);


    Optional<UserEntity> findByUserId(Long Long);
}

