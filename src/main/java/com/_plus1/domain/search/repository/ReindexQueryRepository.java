package com._plus1.domain.search.repository;

import com._plus1.common.entity.*;
import com._plus1.domain.search.model.dto.row.AlbumIndexRow;
import com._plus1.domain.search.model.dto.row.ArtistIndexRow;
import com._plus1.domain.search.model.dto.row.SongIndexRow;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReindexQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QSong song = QSong.song;
    private final QAlbum album = QAlbum.album;
    private final QArtist artist = QArtist.artist;

    private final QSongArtist songArtist = QSongArtist.songArtist;
    private final QAlbumArtist albumArtist = QAlbumArtist.albumArtist;


    public List<SongIndexRow> fetchSongRowsAfterId(long lastId, int size) {
        return jpaQueryFactory.select(Projections.constructor(
                        SongIndexRow.class,
                        song.id,
                        song.externalId,
                        song.title,
                        album.title,
                        song.releaseDate,
                        song.playCount
                ))
                .from(song)
                .leftJoin(song.album, album)
                .where(song.id.gt(lastId))
                .orderBy(song.id.asc())
                .limit(size)
                .fetch();
    }

    public Map<Long, List<String>> fetchArtistNamesBySongIds(List<Long> songIds) {
        // 1. (songId, artistName) : 튜플로 가져와서 자바에서 groupBy
        List<Tuple> rows = jpaQueryFactory
                .select(songArtist.song.id, artist.name)
                .from(songArtist)
                .join(songArtist.artist, artist)
                .where(songArtist.song.id.in(songIds))
                .fetch();

        Map<Long, List<String>> map = new HashMap<>();
        for (Tuple t : rows) {
            Long songId = t.get(songArtist.song.id);
            String name = t.get(artist.name);
            map.computeIfAbsent(songId, k -> new ArrayList<>()).add(name);
        }
        return map;
    }

    public List<AlbumIndexRow> fetchAlbumRowsAfterId(long lastId, int size) {
        return jpaQueryFactory.select(Projections.constructor(
                        AlbumIndexRow.class,
                        album.id,
                        album.externalId,
                        album.title,
                        album.releaseDate
                ))
                .from(album)
                .where(album.id.gt(lastId))
                .orderBy(album.id.asc())
                .limit(size)
                .fetch();
    }

    public Map<Long, List<String>> fetchArtistNamesByAlbumIds(List<Long> albumIds) {
        List<Tuple> rows = jpaQueryFactory
                .select(albumArtist.album.id, artist.name)
                .from(albumArtist)
                .join(albumArtist.artist, artist)
                .where(albumArtist.album.id.in(albumIds))
                .fetch();

        Map<Long, List<String>> map = new HashMap<>();
        for (Tuple t : rows) {
            Long albumId = t.get(albumArtist.album.id);
            String name = t.get(artist.name);
            map.computeIfAbsent(albumId, k -> new ArrayList<>()).add(name);
        }
        return map;
    }

    public List<ArtistIndexRow> fetchArtistRowsAfterId(long lastId, int size) {
        return jpaQueryFactory.select(Projections.constructor(
                        ArtistIndexRow.class,
                        artist.id,
                        artist.externalId,
                        artist.name
                ))
                .from(artist)
                .where(artist.id.gt(lastId))
                .orderBy(artist.id.asc())
                .limit(size)
                .fetch();
    }
}
