package com._plus1.domain.search.service;

import com._plus1.common.entity.*;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SearchPredicateFactory {

    // 1. 검색 조건
    // 2. Page, Size, offset()

    public BooleanBuilder forSong(
            String q,
            LocalDate from,
            LocalDate to,
            QSong song,
            QAlbum album,
            QArtist artist,
            QSongArtist songArtist
    ) {
        BooleanBuilder where = new BooleanBuilder();

        // 1. query
        if (q != null && !q.isBlank()) {

            // 1). artist
            BooleanExpression expression = JPAExpressions
                    .selectOne()
                            .from(songArtist)
                    .join(songArtist.artist, artist)
                    .where(songArtist.song.id.eq(song.id)
                            .and(artist.name.contains(q)))
                    .exists();
            where.and(
                    song.title.contains(q)
                            .or(album.title.contains(q))
                            .or(expression)
            );
        }

        // 2. from ~ to
        if (from != null && to != null) where.and(song.releaseDate.between(from, to));
        else if (from != null) where.and(song.releaseDate.goe(from));
        else if (to != null) where.and(song.releaseDate.loe(to));

        return where;
    }

    public BooleanBuilder forAlbum(
            String q, LocalDate from, LocalDate to, QAlbum album, QArtist artist, QAlbumArtist albumArtist) {
        BooleanBuilder where = new BooleanBuilder();

        if (q != null && !q.isBlank()) {

            BooleanExpression expression = JPAExpressions
                    .selectOne()
                            .from(albumArtist)
                    .join(albumArtist.artist, artist)
                    .where(albumArtist.album.id.eq(album.id)
                            .and(artist.name.contains(q)))
                    .exists();
            where.and(album.title.contains(q)
                            .or(expression));
        }

        if (from != null && to != null) where.and(album.releaseDate.between(from, to));
        else if (from != null) where.and(album.releaseDate.goe(from));
        else if (to != null) where.and(album.releaseDate.loe(to));

        return where;
    }

    public BooleanBuilder forArtist(String q, QArtist artist) {
        BooleanBuilder where = new BooleanBuilder();

        if (q != null && !q.isBlank()) {
            where.and(artist.name.contains(q));
        }

        return where;
    }

}