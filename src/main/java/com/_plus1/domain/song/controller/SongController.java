package com._plus1.domain.song.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.common.dto.PageResponse;
import com._plus1.domain.song.model.response.SongOfPopularResponse;
import com._plus1.domain.song.model.response.SongPlayResponse;
import com._plus1.domain.song.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    // Page 사용 시 자동으로 실행되는 count 쿼리 -> 속도가 많이 느려짐
    @GetMapping("/korea")
    public ResponseEntity<CommonResponse<PageResponse<SongOfPopularResponse>>> getKoreanPopularSongs(
            @RequestParam String genre,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "newest") String sort
    ) {
        // 정렬 방향 결정
        Sort.Direction direction = sort.equals("newest")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Pageable 생성 + 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "releaseDate"));

        // Service 호출
        Page<SongOfPopularResponse> pageResult = songService.getKoreanPopular(genre, pageable);

        // PageResponse 생성
        PageResponse<SongOfPopularResponse> pageResponse = PageResponse.from(pageResult);

        // CommonResponse로 감싸서 반환 -> Page공용응답객체 수정해서 리팩토링 가능? # 20260106-김동욱
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(pageResponse,"국내 장르 음악 조회 성공하였습니다."));
    }

    @GetMapping("/global")
    public ResponseEntity<CommonResponse<PageResponse<SongOfPopularResponse>>> getGlobalPopularSongs(
            @RequestParam String genre,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "newest") String sort
    ) {
        // 정렬 방향 결정
        Sort.Direction direction = sort.equals("newest")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Pageable 생성 + 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "releaseDate"));

        // Service 호출
        Page<SongOfPopularResponse> pageResult = songService.getGlobalPopular(genre, pageable);

        // PageResponse 생성
        PageResponse<SongOfPopularResponse> pageResponse = PageResponse.from(pageResult);

        // CommonResponse로 감싸서 반환 -> Page공용응답객체 수정해서 리팩토링 가능? # 20260106-김동욱
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(pageResponse,"해외 장르 음악 조회 성공하였습니다."));
    }

}
