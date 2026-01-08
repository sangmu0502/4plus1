package com._plus1.domain.song.repository;

import com._plus1.common.entity.SongGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongGenreRepository extends JpaRepository<SongGenre, Long> {

    @Query("""
    select sg
    from SongGenre sg
    join fetch sg.genre
    where sg.song.id in :songIds
""")
    List<SongGenre> findBySongIdInFetchGenre(@Param("songIds") List<Long> songIds);

}
