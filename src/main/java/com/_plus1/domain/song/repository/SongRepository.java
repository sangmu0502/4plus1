package com._plus1.domain.song.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com._plus1.common.entity.Song;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByAlbumId(Long albumId);

//    @Query("""
//        select distinct s
//        from Song s
//        join SongGenre sg on sg.song.id = s.id
//        join Genre g on g.id = sg.genre.id
//        where g.genreCode in :genreCodes
//    """)
    // countQuery 명시
    // 기존 JOIN count : JOIN -> row 폭증 -> 정렬 -> distinct -> count
    // exists 기반 count : Song 기준 스캔 -> 조건 만족 여부만 체크 -> 즉시 true / false
    @Query(
            value = """
        select distinct s
        from Song s
        join SongGenre sg on sg.song.id = s.id
        join Genre g on g.id = sg.genre.id
        where g.genreCode in :genreCodes
    """,
            countQuery = """
        select count(distinct s.id)
        from Song s
        where exists (
            select 1
            from SongGenre sg
            join Genre g on g.id = sg.genre.id
            where sg.song.id = s.id
              and g.genreCode in :genreCodes
        )
    """
    )
    Page<Song> findKoreanPopularSongs (@Param("genreCodes") List<String> genreCodes, Pageable pageable);

}
