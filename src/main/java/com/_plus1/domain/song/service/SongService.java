package com._plus1.domain.song.service;

import com._plus1.common.entity.AlbumArtist;
import com._plus1.common.entity.Song;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.album.repository.AlbumArtistRepository;
import com._plus1.domain.song.model.dto.SongDto;
import com._plus1.domain.song.model.response.SongPlayResponse;
import com._plus1.domain.song.model.response.SongTopTenItemResponse;
import com._plus1.domain.song.model.response.SongTopTenResponse;
import com._plus1.domain.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional(readOnly = true)
    public SongTopTenResponse getTopTenSongs() {

        List<Song> songs = songRepository.findTop10ByOrderByPlayCountDesc();

        // 1. albumId 수집
        List<Long> albumIds = songs.stream()
                .map(song -> song.getAlbum().getId())
                .distinct()
                .toList();

        // 2. AlbumArtist 한방 조회
        List<AlbumArtist> albumArtists =
                albumArtistRepository.findByAlbumIdIn(albumIds);

        // 3. albumId -> artistName 리스트 매핑
        Map<Long, List<String>> albumArtistMap =
                albumArtists.stream()
                        .collect(Collectors.groupingBy(
                                aa -> aa.getAlbum().getId(),
                                Collectors.mapping(
                                        aa -> aa.getArtist().getName(),
                                        Collectors.toList()
                                )
                        ));

        // 4. response 생성
        List<SongTopTenItemResponse> results =
                songs.stream()
                        .map(song -> {
                            SongDto dto = SongDto.from(song);

                            List<String> artists =
                                    albumArtistMap.getOrDefault(
                                            song.getAlbum().getId(),
                                            List.of()
                                    );

                            return SongTopTenItemResponse.from(dto, artists);
                        })
                        .toList();

        return new SongTopTenResponse(results);
    }
}
