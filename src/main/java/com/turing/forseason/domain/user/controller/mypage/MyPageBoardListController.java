package com.turing.forseason.domain.user.controller.mypage;

import com.turing.forseason.domain.board.entity.BoardEntity;
import com.turing.forseason.domain.board.service.BoardService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequiredArgsConstructor
@RestController
public class MyPageBoardListController {

    private final BoardService boardService;

    @GetMapping("/mypage/myboard/{userId}")
    public ApplicationResponse<List<BoardEntity>> getBoardByUserId(@PathVariable long userId) {
        // userId로 게시글 검색해서 결과를 리스트로 반환

        List<BoardEntity> boardlist = boardService.findByUserId(userId);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK,boardlist);
    }
}
