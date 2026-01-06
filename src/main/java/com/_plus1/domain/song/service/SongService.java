package com._plus1.domain.song.service;

import com._plus1.common.entity.AlbumArtist;
import com._plus1.common.entity.Song;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.album.repository.AlbumArtistRepository;
import com._plus1.domain.song.model.dto.SongDto;
import com._plus1.domain.song.model.response.*;
import com._plus1.domain.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final AlbumArtistRepository albumArtistRepository;

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

    // 재생 횟수 기준 Top 10 노래 조회
    @Transactional(readOnly = true)
    public SongTopTenResponse getTopTenSongs() {

        List<Song> songs = songRepository.findTop10ByOrderByPlayCountDesc();

        // albumId 수집
        List<Long> albumIds = songs.stream()
                .map(song -> song.getAlbum().getId())
                .distinct()
                .toList();

        // AlbumArtist 조회
        List<AlbumArtist> albumArtists =
                albumArtistRepository.findByAlbumIdIn(albumIds);

        // albumId -> artistName 리스트 매핑
        Map<Long, List<String>> artistMap =
                albumArtists.stream()
                        .collect(Collectors.groupingBy(
                                aa -> aa.getAlbum().getId(),
                                Collectors.mapping(
                                        aa -> aa.getArtist().getName(),
                                        Collectors.toList()
                                )
                        ));

        // response 생성
        List<SongTopTenItemResponse> results =
                songs.stream()
                        .map(song -> {
                            SongDto dto = SongDto.from(song);

                            List<String> artists =
                                    artistMap.getOrDefault(
                                            song.getAlbum().getId(),
                                            List.of()
                                    );

                            return SongTopTenItemResponse.from(dto, artists);
                        })
                        .toList();

        return new SongTopTenResponse(results);
    }

    // 국내, 해외 최신음악 조회 공통 메서드
    public SongLatestResponse getLatestSongsByGenreCodes(List<String> genreCodes) {

        List<Song> songs = songRepository.findLatestDomesticSongs(
                genreCodes,
                PageRequest.of(0, 50)
        );

        // albumId 수집
        List<Long> albumIds = songs.stream()
                .map(song -> song.getAlbum().getId())
                .distinct()
                .toList();

        // AlbumArtist 조회
        List<AlbumArtist> albumArtists =
                albumArtistRepository.findByAlbumIdIn(albumIds);

        // albumId -> artistName 리스트 매핑
        Map<Long, List<String>> artistMap =
                albumArtists.stream()
                        .collect(Collectors.groupingBy(
                                aa -> aa.getAlbum().getId(),
                                Collectors.mapping(
                                        aa -> aa.getArtist().getName(),
                                        Collectors.toList()
                                )
                        ));

        // response 변환
        List<SongLatestItemResponse> results =
                songs.stream()
                        .map(song -> new SongLatestItemResponse(
                                song.getId(),
                                song.getTitle(),
                                artistMap.getOrDefault(
                                        song.getAlbum().getId(),
                                        List.of()
                                )
                        ))
                        .toList();

        return new SongLatestResponse(results);
    }

    // 한국 최신 음악 호출 서비스
    @Transactional(readOnly = true)
    public SongLatestResponse getLatestDomesticSongs() {

        return getLatestSongsByGenreCodes(List.of(
                "GN0100", "GN0200", "GN0300", "GN0400",
                "GN0500", "GN0600", "GN0700", "GN0800"
        ));
    }

    // 해외 최신 음악 호출 서비스
    @Transactional(readOnly = true)
    public SongLatestResponse getLatestGlobalSongs() {

        return getLatestSongsByGenreCodes(List.of(
                "GN0900", "GN1000", "GN1100",
                "GN1200", "GN1300", "GN1400"
        ));
    }
}
