package com.turing.forseason.domain.comment.repository;

import com.turing.forseason.domain.board.entity.BoardEntity;
import com.turing.forseason.domain.comment.entity.CommentEntity;
import com.turing.forseason.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Optional<List<CommentEntity>> findByUser_UserId(Long userId);

}
