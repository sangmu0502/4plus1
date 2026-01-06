package com._plus1.domain.album.repository;

import com._plus1.common.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findTop10ByOrderByReleaseDateDesc();

}
