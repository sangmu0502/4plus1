package com._plus1.domain.album.repository;

import com._plus1.common.entity.SongArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongArtistRepository extends JpaRepository<SongArtist, Long> {

    @Query("""
    select sa
    from SongArtist sa
    join fetch sa.artist
    where sa.song.id in :songIds
""")
    List<SongArtist> findBySongIdInFetchArtistOnly(@Param("songIds") List<Long> songIds);

    @Query("""
        select sa
        from SongArtist sa
        join fetch sa.song
        join fetch sa.artist
        where sa.song.id in :songIds
    """)
    List<SongArtist> findBySongIdInFetchSongAndArtist(@Param("songIds") List<Long> songIds);

    @Query("""
        select sa.song.id, sa.artist.name
         from SongArtist sa
        where sa.song.id in :songIds
    """)
    List<Object[]> findSongIdAndArtistNameBySongIds(@Param("songIds") List<Long> songIds);

}
