package com._plus1.domain.playlist.repository;

import com._plus1.common.entity.PlaylistSong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaylistSongCustomRepository {
    Page<PlaylistSong> findByPlaylistId(Long playlistId, Pageable pageable);
}
