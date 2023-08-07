package com.turing.forseason.repository;

import com.turing.forseason.entity.TalkEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkRepository extends JpaRepository<TalkEntity, Integer> {
    List<TalkEntity> findByTalkLocationOrderByTalkDateDesc(String talkLocation, Pageable pageable);
}
