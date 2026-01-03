package com._plus1.domain.seed.service;

// 부모 저장 -> Map 생성 -> Join Table 저장.

import com._plus1.common.entity.*;
import com._plus1.domain.album.repository.AlbumRepository;
import com._plus1.domain.seed.csv.Csvs;
import com._plus1.domain.seed.repository.ArtistRepository;
import com._plus1.domain.seed.repository.GenreRepository;
import com._plus1.domain.song.repository.SongRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedService{
    private final EntityManager entityManager;

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final SongRepository songRepository;

    // BATCH flush 단위 : 메모리 폭발 방지
    private static final int BATCH = 2000;

    // 1. seedAll : Orchestration.
    @Transactional
    public void seedAll(Path dir, int limit) throws Exception{
        // 1). seed
        seedArtists(dir.resolve("artists.csv"), limit);
        seedAlbums(dir.resolve("albums.csv"), limit);
        seedGenres(dir.resolve("genres.csv"), limit);
        seedSongs(dir.resolve("songs.csv"), limit);

        // 2). externalId -> PK(id) 맵 생성 : 조인 테이블 적재용
        // Artist, Album, Song, Genre
        Map<Long, Long> artistIdMap = artistRepository.loadIdMap();
        Map<Long, Long> albumIdMap = albumRepository.loadIdMap();
        Map<Long, Long> genreIdMap = genreRepository.loadIdMap();
        Map<Long, Long> songIdMap = songRepository.loadIdMap();


        // -- JOIN TABLE -- //
        // 3). SongArtist
        seedSongArtists(dir.resolve("song_artist.csv"), limit, songIdMap, artistIdMap);

        // 4). SongGenre
        seedSongGenres(dir.resolve("song_genre.csv"), limit, songIdMap, genreIdMap);

        // 5). AlbumArtist
        seedAlbumArtist(dir.resolve("album_artist.csv"), limit, albumIdMap, artistIdMap);
    }

    // 2. seedArtists
    private void seedArtists(Path path, int limit) throws Exception{
        // 1). insert 갯수
        int i = 0;
        try(CSVParser parser = Csvs.open(path)){
            for(CSVRecord record : parser){
                // ①. insert 성공 수
                if(Csvs.overLimit(limit, i)) break;

                long externalId = Long.parseLong(record.get("artist_id"));
                String name = record.get("name");

                Artist artist = new Artist(externalId, name);
                entityManager.persist(artist);

                flushClearIfNeeded(++i);

            }

        }
        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] artists inserted={}", i);
    }

    // 3. seedAlbums
    private void seedAlbums(Path path, int limit) throws Exception{

        int i = 0, skipped = 0;
        try(CSVParser parser=Csvs.open(path)){
            for(CSVRecord record : parser){
                if(Csvs.overLimit(limit, i)) break;

                // album, title, release_date, artist_id
                long externalId = Long.parseLong(record.get("album_id"));
                String title = record.get("title");
                String rawReleaseDate = record.get("release_date");
                LocalDate releaseDate = parseReleaseDateOrNull(rawReleaseDate);

                Album album = new Album(externalId, title, releaseDate);
                entityManager.persist(album);

                flushClearIfNeeded(++i);
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] albums inserted={}, skipped={}", i, skipped);
    }

    // 4. seedGenres
    private void seedGenres(Path path, int limit) throws Exception {
        int i = 0;
        try(CSVParser parser = Csvs.open(path)){
            for(CSVRecord record : parser){
                if(Csvs.overLimit(limit, i)) break;

                long externalId = Long.parseLong(record.get("genre_id"));
                String code = record.get("genre_code");
                String name = record.get("genre_name");

                Genre genre = new Genre(externalId, code, name);
                entityManager.persist(genre);
                flushClearIfNeeded(++i);
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] genre inserted={}", i);
    }

    // 5. seedSongs
    private void seedSongs(Path path, int limit) throws Exception {
        // 1). albumPK : albumIdMap : reference로 연결.
        Map<Long, Long> albumIdMap = albumRepository.loadIdMap();
        int i = 0, skipped = 0;
        try(CSVParser parser = Csvs.open(path)){
            for(CSVRecord record : parser){
                if(Csvs.overLimit(limit, i)) break;

                // externalId title, releaseDate, albumExtract, albumReference

                long externalId=Long.parseLong(record.get("song_id"));
                String title = record.get("title");
                String rawReleaseDate = record.get("release_date");
                LocalDate releaseDate = parseReleaseDateOrNull(rawReleaseDate);
                long albumExtract = Long.parseLong(record.get("album_id"));

                // null 우려. 래퍼 클래스 사용.
                Long albumPk = albumIdMap.get(albumExtract);
                if(albumPk == null) {
                    skipped++;
                    continue;
                }

                Album albumReference = entityManager.getReference(Album.class, albumPk);
                Song song = new Song(externalId, title, releaseDate, albumReference);
                entityManager.persist(song);
                flushClearIfNeeded(++i);
            }
        }
        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] songs inserted={}, skipped={}", i, skipped);
    }

    // 6. seedSongArtists
    private void seedSongArtists(Path path, int limit, Map<Long, Long> songIdMap, Map<Long, Long> artistIdMap) throws Exception{
        int i = 0, skipped = 0;
        try(CSVParser parser=Csvs.open(path)){
            for(CSVRecord record : parser){
                if(Csvs.overLimit(limit, i)) break;

                // songExtract, artistExtract, songReference, ArtistReference
                long songExtract = Long.parseLong(record.get("song_id"));
                long artistExtract = Long.parseLong(record.get("artist_id"));

                Long songPk = songIdMap.get(songExtract);
                Long artistPk = artistIdMap.get(artistExtract);

                if(songPk == null || artistPk == null) {
                    skipped++;
                    continue;
                }

                Song songReference = entityManager.getReference(Song.class, songPk);
                Artist artistReference = entityManager.getReference(Artist.class, artistPk);

                entityManager.persist(new SongArtist(songReference, artistReference));

                flushClearIfNeeded(++i);

            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] song_artist inserted={}, skipped={}", i, skipped);
    }

    // 7. seedSongGenres
    private void seedSongGenres(Path path, int limit, Map<Long, Long> songIdMap, Map<Long, Long> genreIdMap) throws Exception {
        int i = 0, skipped = 0;
        try(CSVParser parser = Csvs.open(path)){
            for(CSVRecord record : parser){
                if(Csvs.overLimit(limit, i)) break;

                // Extract - pk - ref
                long songExtract = Long.parseLong(record.get("song_id"));
                long genreExtract = Long.parseLong(record.get("genre_id"));

                Long songPk = songIdMap.get(songExtract);
                Long genrePk = genreIdMap.get(genreExtract);

                if(songPk == null || genrePk == null) {
                    skipped++;
                    continue;
                }

                Song songReference = entityManager.getReference(Song.class, songPk);
                Genre genreReference = entityManager.getReference(Genre.class, genrePk);

                entityManager.persist(new SongGenre(songReference, genreReference));

                flushClearIfNeeded(++i);
            }
        }
        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] song_genre inserted={}, skipped={}", i, skipped);
    }

    // 8. album + artist
    private void seedAlbumArtist(Path path,
                             int limit,
                             Map<Long, Long> albumIdMap,
                             Map<Long, Long> artistIdMap
    ) throws Exception{
        int i = 0, skipped = 0;
        try(CSVParser parser = Csvs.open(path)){
            for(CSVRecord record : parser){
                if(Csvs.overLimit(limit, i)) break;

                // extract
                long albumExtract = Long.parseLong(record.get("album_id"));
                long artistExtract = Long.parseLong(record.get("artist_id"));

                // pk
                Long albumPk = albumIdMap.get(albumExtract);
                Long artistPk = artistIdMap.get(artistExtract);

                // null : wrapper class
                if(albumPk == null || artistPk == null) {
                    skipped++;
                    continue;
                }

                Album albumReference = entityManager.getReference(Album.class, albumPk);
                Artist artistReference = entityManager.getReference(Artist.class, artistPk);

                entityManager.persist(new AlbumArtist(albumReference, artistReference));

                flushClearIfNeeded(++i);
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("[SEED] album_artist inserted={}, skipped={}", i, skipped);
    }

    // 0. 메모리.
    private void flushClearIfNeeded(int i){
        if(i%BATCH == 0){
            entityManager.flush();
            entityManager.clear();
        }
    }

    // 기타 LocalDate
    private LocalDate parseReleaseDateOrNull(String rawDate) {
        // 1. date가 빈 경우 : null
        if(rawDate == null || rawDate.isBlank()) return null;

        // 2. date : 2005-00-00
        // 1). -로 구분하여 배열로 받기.
        String[] p =rawDate.split("-");

        // 2). 배열 3개 초과 : null.
        if(p.length!=3) return null;

        int y = Integer.parseInt(p[0]);
        int m = Integer.parseInt(p[1]);
        int d = Integer.parseInt(p[2]);

        // 3). m, d 0인 경우. : null
        if(m==0 || d==0) return null;

        try{
            return LocalDate.of(y,m,d);
        }catch(Exception e){
            return null;
        }
    }
}