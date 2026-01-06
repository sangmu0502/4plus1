package com._plus1.domain.search.repository;

import com._plus1.common.entity.*;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import com._plus1.domain.search.service.SearchPredicateFactory;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class SearchQueryRepositoryImpl implements SearchQueryRepository {


    private final SearchPredicateFactory searchPredicateFactory;
    private final JPAQueryFactory jpaQueryFactory;
    private final QSong song = QSong.song;
    private final QAlbum album = QAlbum.album;
    private final QSongArtist songArtist = QSongArtist.songArtist;
    private final QArtist artist = QArtist.artist;
    private final QAlbumArtist albumArtist = QAlbumArtist.albumArtist;

    @Override
    public Page<SongItem> searchSongs(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Pageable pageable
    ){
        // 1. where
        BooleanBuilder where = searchPredicateFactory.forSong(query, from, to);

        // 2. sort
        OrderSpecifier<?> orderBy = switch (sort) {
            case POPULAR -> song.playCount.desc();
            case LATEST -> song.releaseDate.desc().nullsLast();
        };

        // 3. items
        List<SongItem> items = jpaQueryFactory.select(
                Projections.constructor(SongItem.class,
                        song.id,
                        song.externalId,
                        song.title,
                        song.playCount))
                .from(song)
                .leftJoin(song.album, album)
                .leftJoin(songArtist).on(songArtist.song.eq(song))
                .leftJoin(songArtist.artist, artist)
                .where(where)
                .distinct()
                .orderBy(orderBy, song.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4. return + total
        return PageableExecutionUtils.getPage(
                items,
                pageable,
                ()-> Optional.ofNullable(
                        jpaQueryFactory
                                .select(song.id.countDistinct())
                                .from(song)
                                .leftJoin(song.album, album)
                                .leftJoin(songArtist).on(songArtist.song.eq(song))
                                .leftJoin(songArtist.artist, artist)
                                .where(where)
                                .fetchOne()
                ).orElse(0L));

    }

    @Override
    public Page<AlbumItem> searchAlbums(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Pageable pageable
    ){
        // 1. where
        BooleanBuilder where = searchPredicateFactory.forAlbum(query, from, to);

        // 2. sort
        OrderSpecifier<?> orderBy = switch (sort) {
            case POPULAR -> album.id.desc();
            case LATEST -> album.releaseDate.desc().nullsLast();
        };

        // 3. items
        // DTO에 어노테이션 붙여서 Projections.constructor 대체 가능.
        List<AlbumItem> items = jpaQueryFactory
                .select(Projections.constructor(
                        AlbumItem.class,
                        album.id,
                        album.externalId,
                        album.title))
                .from(album)
                .leftJoin(albumArtist).on(albumArtist.album.eq(album))
                .leftJoin(albumArtist.artist, artist)
                .where(where)
                .distinct()
                .orderBy(orderBy, album.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4. return + total
        return PageableExecutionUtils.getPage(
                items,
                pageable,
                ()-> Optional.ofNullable(
                        jpaQueryFactory
                                .select(album.id.countDistinct())
                                .from(album)
                                .leftJoin(albumArtist).on(albumArtist.album.eq(album))
                                .leftJoin(albumArtist.artist, artist)
                                .where(where)
                                .fetchOne()
                ).orElse(0L));

    }

    @Override
    public Page<ArtistItem> searchArtists(String query, SearchSort sort, Pageable pageable){
        BooleanBuilder where = searchPredicateFactory.forArtist(query);

        OrderSpecifier<?> orderBy = switch (sort) {
            case POPULAR -> artist.id.desc();
            case LATEST -> artist.id.desc();
        };

        List<ArtistItem> items = jpaQueryFactory
                .select(Projections.constructor(ArtistItem.class,
                        artist.id,
                        artist.externalId,
                        artist.name))
                .from(artist)
                .where(where)
                .orderBy(orderBy, artist.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4. return + total
        return PageableExecutionUtils.getPage(
                items,
                pageable,
                ()-> Optional.ofNullable(
                        jpaQueryFactory
                        .select(artist.id.countDistinct())
                        .from(artist)
                                .where(where)
                                .fetchOne()
                ).orElse(0L));
    }
}
