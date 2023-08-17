package com.turing.forseason.domain.board.dto;

import com.turing.forseason.domain.board.entity.BoardEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {
    private Long boardId;

    private String boardTitle;

    private String boardContent;

    private String boardPicture;

    private Long boardLikeNum;

    private Long boardCommentNum;

    private String boardUserProfilePicture;

    private String boardUserNickname;

    private String boardHashtags;

    private String boardLocation;

    private Long likeId;

    public static BoardDto entityToDto(BoardEntity boardEntity){
        BoardDto boardDto = BoardDto.builder()
                .boardId(boardEntity.getBoardId())
                .boardTitle(boardEntity.getBoardTitle())
                .boardPicture(boardEntity.getBoardPicture())
                .boardContent(boardEntity.getBoardContents())
                .boardHashtags(boardEntity.getBoardHashtags())
                .boardLocation(boardEntity.getBoardLocation())
                .boardCommentNum(boardEntity.getBoardCommentNum())
                .boardLikeNum(boardEntity.getBoardLikeNum())
                .boardUserNickname(boardEntity.getBoardUserNickname())
                .boardUserProfilePicture(boardEntity.getBoardUserProfilePicture())
                .likeId(null)
                .build();
        return boardDto;
    }
}
