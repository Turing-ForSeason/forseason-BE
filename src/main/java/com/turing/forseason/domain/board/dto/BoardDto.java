package com.turing.forseason.domain.board.dto;

import com.turing.forseason.domain.board.entity.BoardEntity;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Builder
    public BoardDto(Long boardId, String boardTitle, String boardContent, String boardPicture, Long boardLikeNum,
                    Long boardCommentNum, String boardUserProfilePicture, String boardUserNickname,
                    String boardHashtags, String boardLocation) {
        this.boardId = boardId;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardPicture = boardPicture;
        this.boardLikeNum = boardLikeNum;
        this.boardCommentNum = boardCommentNum;
        this.boardUserProfilePicture = boardUserProfilePicture;
        this.boardUserNickname = boardUserNickname;
        this.boardHashtags = boardHashtags;
        this.boardLocation = boardLocation;
    }


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
                .build();
        return boardDto;
    }
}
