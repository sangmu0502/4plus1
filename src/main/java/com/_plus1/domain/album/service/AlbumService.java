package com._plus1.domain.album.service;

import com._plus1.common.entity.Album;
import com._plus1.common.entity.AlbumArtist;
import com._plus1.common.entity.Song;
import com._plus1.common.exception.CustomException;
import com._plus1.domain.album.model.response.*;
import com._plus1.domain.album.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com._plus1.common.exception.ErrorCode.ALBUM_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumArtistRepository albumArtistRepository;
    private final SongRepository songRepository;
    private final SongArtistRepository songArtistRepository;
    private final SongGenreRepository songGenreRepository;

    @Transactional(readOnly = true)
    public AlbumListResponse getNewAlbums() {
        // 1. 앨범 발매일 기준 상단 10개 조회 -> 앨범id 추출
        // 2. 10개의 앨범id에 대응하는 가수 join 조회 -> 앨범-가수 추출
        // 3. 앨범id, "가수1, 가수2" 그루핑하여 매핑
        // 4. 기존의 앨범으로부터 id와 title + 매핑 정보로부터 가수 문자열 -> 최종 응답 DTO(리스트 타입)에 담기
        // 5. 리스트 반환

        // 1️. 최신 앨범 10개 조회
        List<Album> albums = albumRepository.findTop10ByOrderByReleaseDateDesc();
        // [Album(id=10), Album(id=9), Album(id=8), ...]

        List<Long> albumIds = albums.stream()
                .map(Album::getId)
                .toList();
        // [1, 2, 3, 4, 5, ...] 이런식으로 앨범id만 추출한 리스트

        // 2️. 앨범-가수 조인 조회
        List<AlbumArtist> albumArtists =
                albumArtistRepository.findByAlbumIdIn(albumIds);
        // [ (album=1, artist="..."), (album=2, artist="..."), (album=3, artist="..."), ...]

        // 3️. albumId, "가수1, 가수2" 매핑
        Map<Long, String> albumArtistMap =
                albumArtists.stream() // AlbumArtist(albumId=1, artist="..."), AlbumArtist(albumId=2, artist="..."), ...
                        .collect(Collectors.groupingBy(
                                aa -> aa.getAlbum().getId(), // AlbumArtist를 동일 albumId끼리 묶기
                                Collectors.mapping(
                                        aa -> aa.getArtist().getName(), // AlbumArtist를 String타입 가수명으로 받기
                                        Collectors.joining(", ") // 여러 개의 가수명을 ", "로 연결
                                )
                        ));

        // 4️. 응답 DTO 생성
        List<AlbumItemDto> items = albums.stream()
                .map(album -> AlbumItemDto.from(
                        album,  // id, title
                        albumArtistMap.getOrDefault(album.getId(), "")  // 앨범id에 해당하는 가수명 문자열
                ))
                .toList();  // [id, title, artists] 리스트

        return AlbumListResponse.from(items);
    }

    @Transactional(readOnly = true)
    public AlbumDetailsResponse getAlbumDetails(Long albumId) {
        // 1. albumId에 해당하는 앨범의 albumId, title, releaseDate 가져오기 (Album 정보 통째로 가져와서 나중에 뿌리기)
        // 2. albumArtistName은 앨범의 첫번째 가수로 가져오기
        // 3. albumId에 해당하는 곡 List<Song> 조회 (songs 테이블에 앨범id가 존재)
        // 4. 곡-가수 조회 (songIds로 접근 -> SongArtist의 id, 이름 리스트 가져옴)
        // 5. List<AlbumSongResponse>에 필요한 정보 채우기 (id, title, artist)
        // 6. List<AlbumGenreResponse>에 필요한 정보 채우기 (songIds로 접근 -> 만들어둔 전용 레포지토리에서 쿼링)
        // 7. 최종 응답 DTO 반환
        // 1번 과정으로 얻은 정보 -> albumId + title + releaseDate
        // 2번 과정으로 얻은 정보 -> albumArtistName
        // 3번+6번 genreResponses ->  장르코드 + 장르명
        // 3번+4번+5번 songResponses -> songId + title + artists

        // 1. 앨범 조회 + 예외 처리
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ALBUM_NOT_FOUND));

        // 2. 앨범 대표 가수 조회 (첫 번째 가수)
        List<AlbumArtist> albumArtists =
                albumArtistRepository.findByAlbumId(albumId);

        // 내가 모든 데이터를 일일이 보진 않았는데, 혹시나 가수명이 Null일 경우를 대비
        // 이부분은 나중에 수정해도 되긴함
        // 삼항 연산자로 데이터가 없을 경우 공란으로 설정, 있을 경우 첫번째 녀석을 대표 가수로 설정
        String albumArtistName = albumArtists.isEmpty()
                ? ""
                : albumArtists.get(0).getArtist().getName();

        // 3. 앨범에 속한 곡 조회
        List<Song> songs = songRepository.findByAlbumId(albumId);

        List<Long> songIds = songs.stream()
                .map(Song::getId)
                .toList();

        // 4. 곡 - 가수 조회
        Map<Long, List<String>> songArtistMap =
                songArtistRepository.findBySongIdInFetchArtist(songIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                sa -> sa.getSong().getId(),
                                Collectors.mapping(
                                        sa -> sa.getArtist().getName(),
                                        Collectors.toList()
                                )
                        ));

        // 5. 곡 DTO 생성
        List<AlbumSongDto> songResponses = songs.stream()
                .map(song -> AlbumSongDto.from(
                        song.getId(),
                        song.getTitle(),
                        songArtistMap.getOrDefault(song.getId(), List.of())
                ))
                .toList();

        // 6. 장르 조회 (중복 제거)
        List<AlbumGenreDto> genreResponses =
                songGenreRepository.findBySongIdInFetchGenre(songIds)
                        .stream()
                        .collect(Collectors.toMap(
                                sg -> sg.getGenre().getId(),   // genre PK 기준
                                sg -> AlbumGenreDto.from(
                                        sg.getGenre().getGenreCode(),
                                        sg.getGenre().getGenreName()
                                ),
                                (existing, duplicate) -> existing
                        ))
                        .values()
                        .stream()
                        .toList();


        // 7. 최종 DTO 반환
        return AlbumDetailsResponse.from(
                album, // albumId + title + releaseDate
                albumArtistName, // albumArtistName
                genreResponses, // 장르코드 + 장르명
                songResponses // songId + title + artists
        );

    }
}
