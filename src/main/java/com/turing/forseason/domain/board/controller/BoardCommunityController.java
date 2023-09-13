package com.turing.forseason.domain.board.controller;

import com.turing.forseason.domain.board.dto.BoardDto;
import com.turing.forseason.domain.board.entity.BoardEntity;
import com.turing.forseason.domain.board.entity.BoardLikeEntity;
import com.turing.forseason.domain.board.service.BoardService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import com.turing.forseason.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoardCommunityController {
    private final BoardService boardService;

    @GetMapping("/board/boardlist")
    public ApplicationResponse<Page<BoardDto>> getBoardList(Pageable pageable) {
        Page<BoardDto> boardDtoPage = boardService.getBoardDtoPage(pageable);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, boardDtoPage);
    }


    // 밑에 두개는 테스트 용
    @GetMapping("/make/board")
    public ApplicationResponse<BoardEntity> example(@RequestParam("contents") String contents, @RequestParam("title") String title, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, boardService.createExample(contents, title, principalDetails.getUser().getUserId()));
    }

    @GetMapping("/make/boardlike")
    public ApplicationResponse<BoardLikeEntity> example2(@RequestParam("boardId")Long boardId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, boardService.createExample2(boardId, principalDetails.getUser().getUserId()));
    }
}