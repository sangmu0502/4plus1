package com._plus1.domain.song.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com._plus1.common.entity.Song;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByAlbumId(Long albumId);

    List<Song> findTop10ByOrderByPlayCountDesc();

    @Query("""
        select distinct s
        from Song s
        join SongGenre sg on sg.song = s
        join sg.genre g
        where g.genreCode in :genreCodes
        order by s.releaseDate desc
    """)
    List<Song> findLatestDomesticSongs(
            @Param("genreCodes") List<String> genreCodes,
            Pageable pageable
    );
}
