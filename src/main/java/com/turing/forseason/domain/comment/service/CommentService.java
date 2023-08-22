package com.turing.forseason.domain.comment.service;

import com.turing.forseason.domain.comment.entity.CommentEntity;
import com.turing.forseason.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentEntity> findByUserId(Long userId) {

        List<CommentEntity> comment = commentRepository.findByUser_UserId(userId).orElseThrow(
                () -> new NoSuchElementException("해당 사용자의 댓글이 존재하지 않습니다.")
        );

        return comment;
    }
}
