package com._plus1.domain.search.service;

import com._plus1.common.entity.QAlbum;
import com._plus1.common.entity.QArtist;
import com._plus1.common.entity.QSong;
import com._plus1.common.entity.QSongArtist;

import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SearchPredicateFactory {

    // 1. 검색 조건
    // 2. Page, Size, offset()
    private final QAlbum album = QAlbum.album;
    private final QArtist artist = QArtist.artist;
    private final QSong song = QSong.song;

    public BooleanBuilder forSong(String q, LocalDate from, LocalDate to) {
        BooleanBuilder where = new BooleanBuilder();

        // 1. query
        if (q != null && !q.isBlank()) {
            where.and(
                    song.title.containsIgnoreCase(q)
                            .or(album.title.containsIgnoreCase(q))
                            .or(artist.name.containsIgnoreCase(q))
            );
        }

        // 2. from ~ to
        if (from != null && to != null) where.and(song.releaseDate.between(from, to));
        else if (from != null) where.and(song.releaseDate.goe(from));
        else if (to != null) where.and(song.releaseDate.loe(to));

        return where;
    }

    public BooleanBuilder forAlbum(String q, LocalDate from, LocalDate to) {
        BooleanBuilder where = new BooleanBuilder();

        if (q != null && !q.isBlank()) {
            where.and(album.title.containsIgnoreCase(q)
                            .or(artist.name.containsIgnoreCase(q)));
        }

        if (from != null && to != null) where.and(album.releaseDate.between(from, to));
        else if (from != null) where.and(album.releaseDate.goe(from));
        else if (to != null) where.and(album.releaseDate.loe(to));

        return where;
    }

    public BooleanBuilder forArtist(String q) {
        BooleanBuilder where = new BooleanBuilder();

        if (q != null && !q.isBlank()) {
            where.and(artist.name.containsIgnoreCase(q));
        }

        return where;
    }

}