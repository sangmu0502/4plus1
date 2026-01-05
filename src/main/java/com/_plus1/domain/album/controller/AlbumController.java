package com._plus1.domain.album.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.domain.album.model.response.AlbumDetailsResponse;
import com._plus1.domain.album.model.response.AlbumListResponse;
import com._plus1.domain.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    // 최신 앨범 조회 (release_date 기준 상위 10개)
    @GetMapping("/new")
    public ResponseEntity<CommonResponse<AlbumListResponse>> getNewAlbums() {
        AlbumListResponse result = albumService.getNewAlbums();
        return ResponseEntity.ok().body(CommonResponse.success(result, "최신 앨범 10개 조회 성공"));
    }

    // 앨범 상세 조회
    @GetMapping("/{albumId}")
    public ResponseEntity<CommonResponse<AlbumDetailsResponse>> getAlbumsDetails(@PathVariable Long albumId) {
        AlbumDetailsResponse result = albumService.getAlbumDetails(albumId);
        return ResponseEntity.ok().body(CommonResponse.success(result, "앨범 상세 조회 성공"));
    }

}
