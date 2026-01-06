package com._plus1.domain.song.service;

import com._plus1.common.entity.Song;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.album.repository.SongArtistRepository;
import com._plus1.domain.song.model.dto.SongDto;
import com._plus1.domain.song.model.enums.GlobalPopularGenreCode;
import com._plus1.domain.song.model.enums.KoreanPopularGenreCode;
import com._plus1.domain.song.model.response.SongOfPopularResponse;
import com._plus1.domain.song.model.response.SongPlayResponse;
import com._plus1.domain.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongArtistRepository songArtistRepository;

    // 노래 재생 api

    @Transactional
    public SongPlayResponse playSongs(Long songId) {

        // 노래 조회
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(ErrorCode.SONG_NOT_FOUND));

        // 재생 수 증가
        song.increasePlayCount();

        return SongPlayResponse.from(SongDto.from(song));
    }

    // 국내 대중(Popular, POP이 아님)음악 조회
    @Transactional(readOnly = true)
    public Page<SongOfPopularResponse> getKoreanPopular(String genreName, Pageable pageable) {

        // 1. 장르명 -> Enum 매핑
        KoreanPopularGenreCode genre = resolveGenre(genreName);

        // 2. 장르 코드 목록
        List<String> genreCodes = genre.getGenreCodes();

        // 3. Song 조회
        Page<Song> songPage =
                songRepository.findKoreanPopularSongs(genreCodes, pageable);

        if (songPage.isEmpty()) {
            throw new CustomException(ErrorCode.SONG_NOT_FOUND);
        }

        // 4. songIds 추출 -> 현재 페이지에 포함된 곡 ID들
        List<Long> songIds = songPage.getContent()
                .stream()
                .limit(pageable.getPageSize()) // 필요없나?
                .map(Song::getId)
                .toList();

        // 5. 곡 - 가수 매핑
        Map<Long, List<String>> songArtistMap =
                songArtistRepository.findBySongIdInFetchArtistOnly(songIds)
                        .stream()
                        .collect(Collectors.groupingBy( // groupingBy를 사용해서 곡 ID 기준으로 묶기
                                sa -> sa.getSong().getId(), // Map의 key
                                Collectors.mapping( // Map의 value
                                        sa -> sa.getArtist().getName(),
                                        Collectors.toList() // songArtistMap 리스트
                                )
                        ));

        // 6. DTO 변환
        List<SongOfPopularResponse> content =
                songPage.getContent()
                        .stream()
                        .map(song -> SongOfPopularResponse.from(
                                song, // 곡 기본 정보 (id, title, releaseDate)
                                songArtistMap.getOrDefault(song.getId(), List.of()) // 가수가 존재하면 → [가수], 없으면 빈 리스트
                        ))
                        .toList(); // Stream 결과를 List<SongOfPopularResponse>로 변환
        // Page 유지해서 반환
        return new PageImpl<>(content, pageable, songPage.getTotalElements());

    }


    // 해외POP음악 조회
    @Transactional(readOnly = true)
    public Page<SongOfPopularResponse> getGlobalPopular(
            String genreName,
            Pageable pageable
    ) {

        // 1. 장르명 -> Global Enum 매핑
        GlobalPopularGenreCode genre = resolveGlobalGenre(genreName);

        // 2. 장르 코드 목록
        List<String> genreCodes = genre.getGenreCodes();

        // 3. Song 조회 (Page 유지)
        Page<Song> songPage =
                songRepository.findKoreanPopularSongs(genreCodes, pageable);
        // 쿼리 재사용 (장르 코드만 다름)

        if (songPage.isEmpty()) {
            throw new CustomException(ErrorCode.SONG_NOT_FOUND);
        }

        // 4. songIds 추출
        List<Long> songIds = songPage.getContent()
                .stream()
                .map(Song::getId)
                .toList();

        // 5. 곡 - 가수 매핑
        Map<Long, List<String>> songArtistMap =
                songArtistRepository.findBySongIdInFetchArtistOnly(songIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                sa -> sa.getSong().getId(),
                                Collectors.mapping(
                                        sa -> sa.getArtist().getName(),
                                        Collectors.toList()
                                )
                        ));

        // 6. DTO 변환
        List<SongOfPopularResponse> content =
                songPage.getContent()
                        .stream()
                        .map(song -> SongOfPopularResponse.from(
                                song,
                                songArtistMap.getOrDefault(song.getId(), List.of())
                        ))
                        .toList();

        return new PageImpl<>(content, pageable, songPage.getTotalElements());
    }

    // 요청 파라미터로 받은 장르명 문자열 -> Enum 변환 로직 + (존재 여부 + 한국 대중음악 검증)
    private KoreanPopularGenreCode resolveGenre(String genreName) {
                            // Enum에 정의된 모든 장르 값들을 배열로 반환 -> 이 배열을 스트림
        return Arrays.stream(KoreanPopularGenreCode.values())
                            // Enum에 정의된 displayName과 일치한 것만 통과
                .filter(g -> g.getDisplayName().equals(genreName))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_GENRE));
    }

    // 요청 파라미터로 받은 장르명 문자열 -> Enum 변환 로직 + (존재 여부 + 해외 POP음악 검증)
    private GlobalPopularGenreCode resolveGlobalGenre(String genreName) {
        return Arrays.stream(GlobalPopularGenreCode.values())
                .filter(g -> g.getDisplayName().equals(genreName))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_GENRE));
    }

}
