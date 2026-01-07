package com._plus1.domain.playlist.service;

import com._plus1.common.entity.Playlist;
import com._plus1.common.entity.PlaylistSong;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.User;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.common.security.UserDetailsImpl;
import com._plus1.domain.album.repository.SongArtistRepository;
import com._plus1.domain.playlist.model.dto.request.PlaylistCreateRequest;
import com._plus1.domain.playlist.model.dto.request.PlaylistUpdateRequest;
import com._plus1.domain.playlist.model.dto.response.*;
import com._plus1.domain.playlist.repository.PlaylistRepository;
import com._plus1.domain.playlist.repository.PlaylistSongRepository;
import com._plus1.domain.song.repository.SongRepository;
import com._plus1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongArtistRepository songArtistRepository;
    private final SongRepository songRepository;

    // 플리 생성
    @Transactional
    public PlaylistResponse savePlaylist(Long userId, PlaylistCreateRequest request) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = new Playlist(request.getTitle(), request.getDescription(), loginUser);

        playlistRepository.save(playlist);

        return PlaylistResponse.from(playlist);
    }

    // 내 플리 목록 가져오기
    @Transactional(readOnly = true)
    public PlaylistListResponse getMyPlaylists(Long userId) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Playlist> playlists = playlistRepository
                .findByUserIdOrderByUpdatedAtDesc(loginUser.getId());

        List<PlaylistListItemResponse> items = playlists.stream()
                .map(p -> new PlaylistListItemResponse(
                        p.getId(),
                        p.getTitle(),
                        playlistSongRepository.countByPlaylistId(p.getId()),
                        p.getCreatedAt(),
                        p.getUpdatedAt()
                ))
                .toList();

        return PlaylistListResponse.of(items);
    }


    // 내 플리 상세 정보 가져오기
    @Transactional(readOnly = true)
    public PlaylistDetailResponse getPlaylistDetail(Long userId, Long playlistId) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 내 플레이리스트만 조회 가능
        if (!playlist.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        long songCount = playlistSongRepository.countByPlaylistId(playlistId);

        return PlaylistDetailResponse.from(playlist, songCount);
    }

    // 플리 정보 수정
    @Transactional
    public PlaylistUpdateResponse updatePlaylist(Long userId, Long playlistId, PlaylistUpdateRequest request) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 내 플레이리스트만 수정 가능
        if (!playlist.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        playlist.update(request.getTitle(), request.getDescription());


        return PlaylistUpdateResponse.from(playlist);
    }

    // 플리 삭제
    @Transactional
    public void deletePlaylist(Long userId, Long playlistId) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 내 플리만 삭제 가능
        if (!playlist.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        // 플리 안에 곡 먼저 삭제
        playlistSongRepository.deleteByPlaylistId(playlistId);
        playlistRepository.delete(playlist);
    }

    // 플리에 노래 추가
    @Transactional
    public PlaylistAddSongResponse addSongToPlaylist(Long userId, Long playlistId, Long songId) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 내 플레이리스트만 수정 가능
        if (!playlist.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(ErrorCode.SONG_NOT_FOUND));

        // 중복된 노래 추가 방지(뺄지 말지 고민 중)
        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, songId)) {
            throw new CustomException(ErrorCode.DUPLICATE_SONG);
        }

        // sortOrder = max + 1
        Integer max = playlistSongRepository.findMaxSortOrderByPlaylistId(playlistId);
        int nextOrder = (max == null ? 1 : max + 1);

        PlaylistSong ps = new PlaylistSong(nextOrder, song, playlist);

        playlistSongRepository.save(ps);

        // 추가 후 전체 목록 조회
        List<PlaylistSong> playlistSongs = playlistSongRepository
                .findAllWithSongByPlaylistIdOrderBySortOrder(playlistId);

        // artists 리스트
        List<Long> songIds = playlistSongs.stream()
                .map(p -> p.getSong().getId())
                .distinct()
                .toList();

        Map<Long, List<String>> artistsBySongId = new HashMap<>();

        if (!songIds.isEmpty()) {
            List<Object[]> rows = songArtistRepository.findSongIdAndArtistNameBySongIds(songIds);
            for (Object[] r : rows) {
                Long sId = (Long) r[0];
                String artistName = (String) r[1];
                artistsBySongId.computeIfAbsent(sId, k -> new ArrayList<>()).add(artistName);
            }
        }

        List<PlaylistSongResponse> songResponses = playlistSongs.stream()
                .map(p -> new PlaylistSongResponse(
                        p.getSong().getId(),
                        p.getSong().getTitle(),
                        artistsBySongId.getOrDefault(p.getSong().getId(), List.of())
                ))
                .toList();

        return PlaylistAddSongResponse.from(playlist, songResponses);
    }

    // 플리에서 노래 삭제
    @Transactional
    public PlaylistAddSongResponse removeSongFromPlaylist(Long userId, Long playlistId, Long songId) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 내 플레이리스트만 수정 가능
        if (!playlist.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        PlaylistSong target = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_SONG_NOT_FOUND));

        playlistSongRepository.delete(target);

        // 삭제 후 전체 목록 조회
        List<PlaylistSong> playlistSongs = playlistSongRepository
                .findAllWithSongByPlaylistIdOrderBySortOrder(playlistId);

        // artists name list
        List<Long> songIds = playlistSongs.stream()
                .map(ps -> ps.getSong().getId())
                .distinct()
                .toList();

        Map<Long, List<String>> artistsBySongId = new HashMap<>();
        if (!songIds.isEmpty()) {
            List<Object[]> rows = songArtistRepository.findSongIdAndArtistNameBySongIds(songIds);
            for (Object[] r : rows) {
                Long sId = (Long) r[0];
                String artistName = (String) r[1];
                artistsBySongId.computeIfAbsent(sId, k -> new ArrayList<>()).add(artistName);
            }
        }

        List<PlaylistSongResponse> songResponses = playlistSongs.stream()
                .map(ps -> new PlaylistSongResponse(
                        ps.getSong().getId(),
                        ps.getSong().getTitle(),
                        artistsBySongId.getOrDefault(ps.getSong().getId(), List.of())
                ))
                .toList();

        return PlaylistAddSongResponse.from(playlist, songResponses);
    }


    @Transactional(readOnly = true)
    public Page<PlaylistSongItemResponse> getPlaylistSongs(Long userId, Long playlistId, Pageable pageable) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        Page<PlaylistSong> page = playlistSongRepository.findByPlaylistId(playlistId, pageable);

        // songId만 추출
        List<Long> songIds = page.getContent().stream()
                .map(ps -> ps.getSong().getId())
                .distinct()
                .toList();

        // songId -> artists(name list)
        Map<Long, List<String>> artistsBySongId = new HashMap<>();
        if (!songIds.isEmpty()) {
            List<Object[]> rows = songArtistRepository.findSongIdAndArtistNameBySongIds(songIds);
            for (Object[] r : rows) {
                Long songId = (Long) r[0];
                String artistName = (String) r[1];
                artistsBySongId.computeIfAbsent(songId, k -> new ArrayList<>()).add(artistName);
            }
        }

        return page.map(ps -> PlaylistSongItemResponse.from(ps, artistsBySongId));
    }



}