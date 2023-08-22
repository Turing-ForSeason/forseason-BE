package com.turing.forseason.domain.user.controller.mypage;

import com.turing.forseason.domain.comment.entity.CommentEntity;
import com.turing.forseason.domain.comment.service.CommentService;
import com.turing.forseason.global.dto.ApplicationResponse;
import com.turing.forseason.global.errorException.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MyPageCommentListController {

    private final CommentService commentService;

    @GetMapping("/mypage/mycomment/{userId}")
    public ApplicationResponse<List<CommentEntity>> getBoardByUserId(@PathVariable long userId) {
        // userId로 댓글 검색해서 결과를 리스트로 반환

        List<CommentEntity> commentlist = commentService.findByUserId(userId);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK,commentlist);
    }
}

