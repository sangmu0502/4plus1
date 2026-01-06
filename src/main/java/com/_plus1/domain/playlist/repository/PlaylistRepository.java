package com._plus1.domain.playlist.repository;

import com._plus1.common.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByUserIdOrderByUpdatedAtDesc(Long userId);
}