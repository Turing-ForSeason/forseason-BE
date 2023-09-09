package com.turing.forseason.domain.board.controller;

import com.turing.forseason.domain.board.entity.BoardEntity;
import org.springframework.http.ResponseEntity;
import com.turing.forseason.domain.board.service.BoardService;
import com.turing.forseason.domain.user.entity.UserEntity;
import com.turing.forseason.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/community/coordi")
@RequiredArgsConstructor
public class BoardCoordiUploadController {

    // BoardService 인스턴스 주입
    private final BoardService boardService;
    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/upload/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        // Cloudinary에 업로드한 이미지 URL 가져오기
        String imageUrl = getImageUrlFromCloudinary(image.getBytes());
        // 이미지 업로드 성공 응답
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/upload/board")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> uploadBoard(@RequestPart BoardUploadRequest request,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        UserEntity user = principalDetails.getUser();
        BoardEntity boardEntity = request.toBoardEntity();
        boardEntity.setUser(user);

        // 이미지 업로드를 직접 처리
        if (request.getBoardPicture() != null) {
            String imageUrl = getImageUrlFromCloudinary(request.getBoardPicture().getBytes());
            boardEntity.setBoardPicture(imageUrl); // 이미지 URL을 게시글 엔티티에 저장
        }

        boardService.uploadCoordiBoard(boardEntity);

        // 업로드 성공 응답
        return ResponseEntity.ok("Board uploaded successfully");
    }

    // 여러 이미지 업로드 및 URL 배열 반환 메서드
// 여러 이미지 업로드 및 URL 리스트 반환 메서드
    private List<String> getImageUrlsFromCloudinary(MultipartFile[] imageFiles) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            byte[] imageBytes = imageFile.getBytes();
            String imageUrl = getImageUrlFromCloudinary(imageBytes);
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    private String getImageUrlFromCloudinary(byte[] imageBytes) throws IOException {
        // cloudinary 이미지 업로드 파라미터 설정
        Map<String, Object> params = Collections.singletonMap("public_id", UUID.randomUUID().toString());
        // 이미지 byte 코드와 생성한 UUID 업로드
        Map<String, String> uploadResult = cloudinary.uploader().upload(imageBytes, params);
        // 이미지 secure URL 가져오기
        return uploadResult.get("secure_url");
    }

    /* 게시글 업로드 요청 처리 클래스 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ */
    static class BoardUploadRequest {
        private String boardTitle;
        private String boardContents;
        private String boardHashtags;
        private MultipartFile boardPicture;

        public String getBoardTitle() { return boardTitle; }
        public void setBoardTitle(String boardTitle) { this.boardTitle = boardTitle; }

        public String getBoardContents() { return boardContents; }
        public void setBoardContents(String boardContents) { this.boardContents = boardContents; }

        public String getBoardHashtags() { return boardHashtags; }
        public void setBoardHashtags(String boardHashtags) { this.boardHashtags = boardHashtags; }

        public MultipartFile getBoardPicture() { return boardPicture; }
        public void setBoardPicture(MultipartFile boardPicture) { this.boardPicture = boardPicture; }

        public BoardEntity toBoardEntity() {
            return BoardEntity.builder()
                    .boardTitle(boardTitle)
                    .boardContents(boardContents)
                    .boardHashtags(boardHashtags)
                    .build();
        }
    }
}