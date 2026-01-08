package com._plus1.domain.playlist.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.common.security.UserDetailsImpl;
import com._plus1.domain.playlist.model.dto.request.PlaylistCreateRequest;
import com._plus1.domain.playlist.model.dto.request.PlaylistUpdateRequest;
import com._plus1.domain.playlist.model.dto.response.*;
import com._plus1.domain.playlist.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    // 플리 생성
    @PostMapping
    public ResponseEntity<CommonResponse<PlaylistResponse>> createPlaylist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PlaylistCreateRequest request
    ) {
        PlaylistResponse response = playlistService.savePlaylist(userDetails.getUser().getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(response, "사용자 플레이리스트 생성이 완료되었습니다."));
    }

    // 내 플리 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<PlaylistListResponse>> getMyPlaylists(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        PlaylistListResponse response = playlistService.getMyPlaylists(userDetails.getUser().getId());

        return ResponseEntity.ok()
                .body(CommonResponse.success(response, "사용자 플레이리스트 조회가 완료되었습니다."));
    }

    // 플리 상세 조회
    @GetMapping("/{playlistId}")
    public ResponseEntity<CommonResponse<PlaylistDetailResponse>> getPlaylistDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long playlistId
    ) {
        PlaylistDetailResponse response = playlistService.getPlaylistDetail(userDetails.getUser().getId(), playlistId);

        return ResponseEntity.ok()
                .body(CommonResponse.success(response, "플레이리스트 상세조회가 완료되었습니다."));
    }


    // 플리 수정
    @PutMapping("/{playlistId}")
    public ResponseEntity<CommonResponse<PlaylistUpdateResponse>> updatePlaylist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long playlistId,
            @Valid @RequestBody PlaylistUpdateRequest request
    ) {
        PlaylistUpdateResponse response = playlistService.updatePlaylist(userDetails.getUser().getId(), playlistId, request);

        return ResponseEntity.ok()
                .body(CommonResponse.success(response, "사용자 플레이리스트 수정이 완료되었습니다."));
    }

    // 플리 삭제
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<CommonResponse<Void>> deletePlaylist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long playlistId) {
        playlistService.deletePlaylist(userDetails.getUser().getId(), playlistId);

        return ResponseEntity.ok()
                .body(CommonResponse.success(null, "해당 플레이리스트를 삭제하였습니다."));
    }


    // 플리에 곡 추가
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<CommonResponse<PlaylistAddSongResponse>> addSongToPlaylist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long playlistId,
            @PathVariable Long songId
    ) {
        PlaylistAddSongResponse response = playlistService.addSongToPlaylist(userDetails.getUser().getId(), playlistId, songId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(response, "플레이리스트에 해당 곡이 추가되었습니다."));
    }

    // 플리에서 곡 삭제
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<CommonResponse<PlaylistAddSongResponse>> removeSongFromPlaylist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long playlistId,
            @PathVariable Long songId
    ) {
        PlaylistAddSongResponse response = playlistService.removeSongFromPlaylist(userDetails.getUser().getId(), playlistId, songId);

        return ResponseEntity.ok()
                .body(CommonResponse.success(response, "플레이리스트에서 해당 곡이 삭제되었습니다."));
    }

    // 각 플리 별 곡 전체 불러오기(페이지)
    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<CommonResponse<Page<PlaylistSongItemResponse>>> getPlaylistSongs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long playlistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder"));

        Page<PlaylistSongItemResponse> response = playlistService.getPlaylistSongs(userDetails.getUser().getId(), playlistId, pageable);

        return ResponseEntity.ok()
                .body(CommonResponse.success(response, "플레이리스트 노래 목록 조회가 완료되었습니다."));
    }





}
