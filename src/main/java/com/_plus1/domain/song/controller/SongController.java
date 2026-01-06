package com._plus1.domain.song.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.domain.song.model.response.SongPlayResponse;
import com._plus1.domain.song.model.response.SongTopTenResponse;
import com._plus1.domain.song.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/{songId}/play")
    public ResponseEntity<CommonResponse<SongPlayResponse>> playSongs(
            @PathVariable Long songId
    ) {
        SongPlayResponse response = songService.playSongs(songId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(response, "재생 성공하였습니다."));
    }

    @GetMapping("/top")
    public ResponseEntity<CommonResponse<SongTopTenResponse>> getTopTenSongs() {

        SongTopTenResponse response = songService.getTopTenSongs();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(response, "인기 음악 조회를 성공하였습니다."));
    }
}
