package com._plus1.domain.like.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.domain.like.model.response.LikeResponse;
import com._plus1.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 생성 api
    @PostMapping("/{songId}")
    public ResponseEntity<CommonResponse<LikeResponse>> createLike(
            @PathVariable Long songId
    ) {
        LikeResponse response = likeService.createLike(songId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(response, "좋아요 생성 성공했습니다."));
    }


    // 좋아요 삭제 api
    @DeleteMapping("/{songId}")
    public ResponseEntity<CommonResponse<Void>> deleteLike(
            @PathVariable Long songId
    ) {
        likeService.deleteLike(songId);

        return ResponseEntity.ok(
                CommonResponse.success(null, "좋아요 삭제 성공했습니다.")
        );
    }
}
