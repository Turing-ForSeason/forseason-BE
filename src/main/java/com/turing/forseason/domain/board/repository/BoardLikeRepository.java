package com.turing.forseason.domain.board.repository;

import com.turing.forseason.domain.board.entity.BoardLikeEntity;
import com.turing.forseason.domain.talk.entity.TalkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {
    Optional<BoardLikeEntity>  findByUser_UserIdAndBoard_BoardId(Long userId, Long boardId);

    BoardLikeEntity save(BoardLikeEntity boardLikeEntity);
}
