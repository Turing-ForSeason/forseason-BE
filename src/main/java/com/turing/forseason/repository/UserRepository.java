package com.turing.forseason.repository;

import com.turing.forseason.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    public Optional<UserEntity> findByUserEmail(String userEmail);
}
