package com.turing.forseason.domain.talk.repository;

import com.turing.forseason.domain.talk.entity.TalkEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkRepository extends JpaRepository<TalkEntity, Long> {
    List<TalkEntity> findByTalkLocationOrderByCreatedAtDesc(String talkLocation, Pageable pageable);

    List<TalkEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
