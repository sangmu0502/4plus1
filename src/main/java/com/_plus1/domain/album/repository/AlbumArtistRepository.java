package com._plus1.domain.album.repository;

import com._plus1.common.entity.AlbumArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumArtistRepository extends JpaRepository<AlbumArtist,Long> {

    List<AlbumArtist> findByAlbumIdIn(List<Long> albumIds);

    List<AlbumArtist> findByAlbumId(Long albumId);

}
