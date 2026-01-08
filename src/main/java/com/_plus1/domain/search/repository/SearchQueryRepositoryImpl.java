package com._plus1.domain.search.repository;

import com._plus1.common.entity.*;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.cache.SearchKey;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import com._plus1.domain.search.service.SearchPredicateFactory;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com._plus1.domain.search.model.dto.SearchSort.LATEST;
import static com._plus1.domain.search.model.dto.SearchSort.POPULAR;


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
            SearchKey condition
    ){
        // 1. where
        BooleanBuilder where = searchPredicateFactory.forSong(
                condition.q(),
                condition.from(),
                condition.to(),
                song,
                album,
                artist,
                songArtist
        );

        // 2. sort
        OrderSpecifier<?> orderBy = switch (condition.sort()) {
            case POPULAR -> song.playCount.desc();
            case LATEST -> song.releaseDate.desc().nullsLast();
        };

        // 3. pageable
        Pageable pageable = PageRequest.of(condition.page(), condition.size());

        // 4. items
        List<SongItem> items = jpaQueryFactory.select(
                Projections.constructor(SongItem.class,
                        song.id,
                        song.externalId,
                        song.title,
                        song.playCount,
                        song.releaseDate
                ))
                .from(song)
                .leftJoin(song.album, album)
                .where(where)
                .orderBy(orderBy, song.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 5. return + total
        return PageableExecutionUtils.getPage(
                items,
                pageable,
                ()-> Optional.ofNullable(
                        jpaQueryFactory
                                .select(song.id.count())
                                .from(song)
                                .leftJoin(song.album, album)
                                .where(where)
                                .fetchOne()
                ).orElse(0L));

    }

    @Override
    public Page<AlbumItem> searchAlbums(SearchKey condition){
        // 1. where
        BooleanBuilder where = searchPredicateFactory.forAlbum(
                condition.q(), condition.from(), condition.to(), album, artist, albumArtist);

        // 2. sort
        OrderSpecifier<?> orderBy = switch (condition.sort()) {
            case POPULAR -> album.id.desc();
            case LATEST -> album.releaseDate.desc().nullsLast();
        };

        // 3. pageable
        Pageable pageable = PageRequest.of(condition.page(), condition.size());

        // 4. items
        // DTO에 어노테이션 붙여서 Projections.constructor 대체 가능.
        List<AlbumItem> items = jpaQueryFactory
                .select(Projections.constructor(
                        AlbumItem.class,
                        album.id,
                        album.externalId,
                        album.title, album.releaseDate))
                .from(album)
                .where(where)
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
                                .select(album.id.count())
                                .from(album)
                                .where(where)
                                .fetchOne()
                ).orElse(0L));
    }

    @Override
    public Page<ArtistItem> searchArtists(SearchKey condition){
        BooleanBuilder where = searchPredicateFactory.forArtist(condition.q(), artist);

        OrderSpecifier<?> orderBy = switch (condition.sort()) {
            case POPULAR -> artist.id.desc();
            case LATEST -> artist.id.desc();
        };

        Pageable pageable = PageRequest.of(condition.page(), condition.size());

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
                        .select(artist.id.count())
                        .from(artist)
                                .where(where)
                                .fetchOne()
                ).orElse(0L));
    }

    @Override
    public Slice<SongItem> searchSongsSlice(SearchKey condition){
        // 1. where
        BooleanBuilder where = searchPredicateFactory.forSong(
                condition.q(), condition.from(), condition.to(),
                song, album, artist, songArtist);

        // 2. sort
        OrderSpecifier<?> orderBy = switch (condition.sort()) {
            case POPULAR -> song.playCount.desc();
            case LATEST -> song.releaseDate.desc().nullsLast();
        };

        // 3. pageable
        Pageable pageable = PageRequest.of(condition.page(), condition.size());

        // 4. 다음 페이지 체크용 size
        int size = pageable.getPageSize();

        // 5. rows
        List<SongItem> rows = jpaQueryFactory.select(
                        Projections.constructor(SongItem.class,
                                song.id,
                                song.externalId,
                                song.title,
                                song.playCount, song.releaseDate))
                .from(song)
                .leftJoin(song.album, album)
                .where(where)
                .orderBy(orderBy, song.id.desc())
                .offset(pageable.getOffset()) // Offset의 크기가 크다면 DB 성능저하 발생.
                .limit(size + 1) // 다음이 있는지만 판단.
                .fetch();

        // 5. hasNext
        boolean hasNext = rows.size() > size;

        // The returned list is backed by this list,
        // so non-structural changes in the returned list are reflected in this list, and vice - versa.
        // -> 반환된 리스트는 여전히 연결되어 있으므로, 상호간의 영향을 받는다는 소리.
        if(hasNext) rows = rows.subList(0, size);

        // 6. return
        return new SliceImpl<>(rows, pageable, hasNext);
    }

    @Override
    public Slice<AlbumItem> searchAlbumsSlice(SearchKey condition){
        // 1. where
        BooleanBuilder where = searchPredicateFactory.forAlbum(
                condition.q(), condition.from(), condition.to(), album, artist, albumArtist);

        // 2. sort
        OrderSpecifier<?> orderBy = switch (condition.sort()) {
            case POPULAR -> album.id.desc();
            case LATEST -> album.releaseDate.desc().nullsLast();
        };

        // 3. pageable
        Pageable pageable = PageRequest.of(condition.page(), condition.size());

        // 3. 다음 페이지 체크용 size
        int size = pageable.getPageSize();

        // 4. rows
        // DTO에 어노테이션 붙여서 Projections.constructor 대체 가능.
        List<AlbumItem> rows = jpaQueryFactory
                .select(Projections.constructor(
                        AlbumItem.class,
                        album.id,
                        album.externalId,
                        album.title, album.releaseDate))
                .from(album)
                .where(where)
                .orderBy(orderBy, album.id.desc())
                .offset(pageable.getOffset()) // Offset의 크기가 크다면 DB 성능저하 발생.
                .limit(size + 1) // 다음이 있는지만 판단.
                .fetch();

        // 5. hasNext
        boolean hasNext = rows.size() > size;
        if(hasNext) rows = rows.subList(0, size);

        // 6. return
        return new SliceImpl<>(rows, pageable, hasNext);
    }

    @Override
    public Slice<ArtistItem> searchArtistsSlice(SearchKey condition){
        BooleanBuilder where = searchPredicateFactory.forArtist(condition.q(), artist);

        OrderSpecifier<?> orderBy = switch (condition.sort()) {
            case POPULAR -> artist.id.desc();
            case LATEST -> artist.id.desc();
        };

        Pageable pageable = PageRequest.of(condition.page(), condition.size());

        int size = pageable.getPageSize();

        List<ArtistItem> rows = jpaQueryFactory
                .select(Projections.constructor(ArtistItem.class,
                        artist.id,
                        artist.externalId,
                        artist.name))
                .from(artist)
                .where(where)
                .orderBy(orderBy, artist.id.desc())
                .offset(pageable.getOffset())
                .limit(size + 1)
                .fetch();

        boolean hasNext = rows.size() > size;
        if(hasNext) rows = rows.subList(0, size);

        return new SliceImpl<>(rows, pageable, hasNext);
    }
}
