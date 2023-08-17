package com.turing.forseason.domain.board.repository;

import com.turing.forseason.domain.board.entity.BoardEntity;
import com.turing.forseason.domain.talk.entity.TalkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findAll(Pageable pageable);

    BoardEntity save(BoardEntity boardEntity);

    Optional<BoardEntity> findByBoardId(Long boardId);
}
