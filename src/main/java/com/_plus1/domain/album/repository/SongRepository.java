package com._plus1.domain.album.repository;

import com._plus1.common.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByAlbumId(Long albumId);

}
