package com._plus1.domain.song.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com._plus1.common.entity.Song;

public interface SongRepository extends JpaRepository<Song, Long> {
}
