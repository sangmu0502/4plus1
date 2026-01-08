package com._plus1.domain.playlist.repository;

import com._plus1.common.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long>, PlaylistSongCustomRepository {

    Long countByPlaylistId(Long playlistId);

    void deleteByPlaylistId(Long playlistId);


    boolean existsByPlaylistIdAndSongId(Long playlistId, Long songId);

    @Query("""
        select coalesce(max(ps.sortOrder), 0)
        from PlaylistSong ps
        where ps.playlist.id = :playlistId
    """)
    Integer findMaxSortOrderByPlaylistId(@Param("playlistId") Long playlistId);

    @Query("""
        select ps
        from PlaylistSong ps
        join fetch ps.song s
        where ps.playlist.id = :playlistId
        order by ps.sortOrder asc
    """)
    List<PlaylistSong> findAllWithSongByPlaylistIdOrderBySortOrder(@Param("playlistId") Long playlistId);


    Optional<PlaylistSong> findByPlaylistIdAndSongId(Long playlistId, Long songId);


}
