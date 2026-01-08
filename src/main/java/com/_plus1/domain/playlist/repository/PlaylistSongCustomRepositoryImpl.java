package com._plus1.domain.playlist.repository;

import com._plus1.common.entity.PlaylistSong;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com._plus1.common.entity.QPlaylistSong.playlistSong;
import static com._plus1.common.entity.QSong.song;

@Slf4j
public class PlaylistSongCustomRepositoryImpl implements PlaylistSongCustomRepository{

    private final JPAQueryFactory queryFactory;

    public PlaylistSongCustomRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PlaylistSong> findByPlaylistId(Long playlistId, Pageable pageable){

        List<PlaylistSong> content = queryFactory
                .selectFrom(playlistSong)
                .join(playlistSong.song, song)
                .fetchJoin()
                .where(playlistSong.playlist.id.eq(playlistId))
                .orderBy(playlistSong.sortOrder.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(playlistSong.count())
                .from(playlistSong)
                .where(playlistSong.playlist.id.eq(playlistId))
                .fetchOne();

        long totalElements = (total == null) ? 0L : total;

        return new PageImpl<>(content, pageable, totalElements);


    }
}
