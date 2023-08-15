package com.turing.forseason.domain.user.repository;

import com.turing.forseason.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUserEmail(String userEmail);

    Optional<UserEntity> findByUserId(Long Long);
}
