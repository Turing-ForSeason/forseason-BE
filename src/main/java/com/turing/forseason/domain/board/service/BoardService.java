package com.turing.forseason.domain.board.service;

import com.turing.forseason.domain.board.dto.BoardDto;
import com.turing.forseason.domain.board.entity.BoardEntity;
import com.turing.forseason.domain.board.entity.BoardLikeEntity;
import com.turing.forseason.domain.board.repository.BoardLikeRepository;
import com.turing.forseason.domain.board.repository.BoardRepository;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    private final UserRepository userRepository;

    public Page<BoardEntity> getBoardEntityPage(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Page<BoardDto> getBoardDtoPage(Page<BoardEntity> boardEntityPage, Long userId) {
        Page<BoardDto> boardDtoPage = boardEntityPage.map(BoardDto::entityToDto);
        for (BoardDto item : boardDtoPage.getContent()) {
            Optional<BoardLikeEntity> boardLikeEntity = boardLikeRepository.findByUser_UserIdAndBoard_BoardId(userId, item.getBoardId());
            if(boardLikeEntity.isPresent())item.setLikeId(boardLikeEntity.get().getLikeId());
            else item.setLikeId(null);
        }
        return boardDtoPage;
    }

    public BoardDto entityToDto(BoardEntity boardEntity, Long userId, Long boardId) {
        Optional<BoardLikeEntity> boardLikeEntity = boardLikeRepository.findByUser_UserIdAndBoard_BoardId(userId, boardId);
        Long likeId;
        if(boardLikeEntity.isPresent()) likeId = boardLikeEntity.get().getLikeId();
        else likeId = null;

        BoardDto boardDto = BoardDto.builder()
                .boardId(boardEntity.getBoardId())
                .boardTitle(boardEntity.getBoardTitle())
                .boardPicture(boardEntity.getBoardPicture())
                .boardContents(boardEntity.getBoardContents())
                .boardHashtags(boardEntity.getBoardHashtags())
                .boardLocation(boardEntity.getBoardLocation())
                .boardCommentNum(boardEntity.getBoardCommentNum())
                .boardLikeNum(boardEntity.getBoardLikeNum())
                .boardUserNickname(boardEntity.getBoardUserNickname())
                .boardUserProfilePicture(boardEntity.getBoardUserProfilePicture())
                .likeId(likeId)
                .build();
        return boardDto;
    }

    public List<BoardEntity> findByUserId(Long userId) {
        // 마이페이지 내 글 목록 보기

        List<BoardEntity> board = boardRepository.findByUser_UserId(userId).orElseThrow(
                () -> new NoSuchElementException("해당 사용자의 글이 존재하지 않습니다.")
        );

        return board;
    }

    // uploadCoordiBoard 메서드 수정 중
    public void uploadCoordiBoard(BoardEntity boardEntity) {
        BoardEntity newBoardEntity = BoardEntity.builder()
                .boardTitle(boardEntity.getBoardTitle())
                .boardContents(boardEntity.getBoardContents())
                .boardPicture(boardEntity.getBoardPicture())
                .boardUserProfilePicture(boardEntity.getBoardUserProfilePicture())
                .boardUserNickname(boardEntity.getBoardUserNickname())
                .user(boardEntity.getUser())
                .boardHashtags(boardEntity.getBoardHashtags())
                .boardLocation(boardEntity.getBoardLocation())
                .build();

        boardRepository.save(newBoardEntity);
    }

    // 밑에 2개는 테스트 용
    public BoardEntity createExample(String contents, String title, Long userId) {
        BoardEntity boardEntity=BoardEntity.builder()
                .boardLocation("서울")
                .boardTitle(title)
                .boardPicture(userRepository.findByUserId(userId).get().getImage())
                .boardCommentNum(0L)
                .boardContents(contents)
                .boardHashtags("#example")
                .boardUserNickname(userRepository.findByUserId(userId).get().getUserNickname())
                .boardUserProfilePicture(userRepository.findByUserId(userId).get().getUserNickname())
                .user(userRepository.findByUserId(userId).get())
                .boardLikeNum(0L)
                .build();
        return boardRepository.save(boardEntity);
    }

    public BoardLikeEntity createExample2(Long boardId, Long userId) {
        BoardEntity boardEntity = boardRepository.findByBoardId(boardId).get();
        boardEntity.increaseLikeNum();
        UserEntity user = userRepository.findByUserId(userId).get();
        BoardLikeEntity boardLikeEntity = BoardLikeEntity.builder()
                .user(user)
                .board(boardEntity)
                .build();
        return boardLikeRepository.save(boardLikeEntity);
    }
}
