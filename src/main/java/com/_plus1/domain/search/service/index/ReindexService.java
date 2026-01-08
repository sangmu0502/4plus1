package com._plus1.domain.search.service.index;

import com._plus1.domain.search.model.dto.docs.AlbumDoc;
import com._plus1.domain.search.model.dto.docs.ArtistDoc;
import com._plus1.domain.search.model.dto.docs.SongDoc;
import com._plus1.domain.search.model.dto.row.AlbumIndexRow;
import com._plus1.domain.search.model.dto.row.ArtistIndexRow;
import com._plus1.domain.search.model.dto.row.SongIndexRow;
import com._plus1.domain.search.repository.ReindexQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReindexService {

    private final ReindexQueryRepository reindexQueryRepository;
    private final EsBulkIndexer esBulkIndexer;

    @Transactional(readOnly = true)
    public void reindexSongs(int batchSize) throws IOException {

        long lastId = 0L;
        int i = 0;
        while (true) {

            List<SongIndexRow> rows = reindexQueryRepository.fetchSongRowsAfterId(lastId, batchSize);
            if (rows.isEmpty()) break;

            List<Long> ids = rows.stream().map(SongIndexRow::id).toList();
            Map<Long, List<String>> artistMap = reindexQueryRepository.fetchArtistNamesBySongIds(ids);

            List<SongDoc> docs = rows.stream().map(r -> {

                String normalTitle = norm(r.title());
                String normalAlbumTitle = norm(r.albumTitle());

                return new SongDoc(
                        r.id(),
                        r.externalId(),
                        r.title(),
                        r.albumTitle(),
                        artistMap.getOrDefault(r.id(), List.of()),
                        r.releaseDate(),
                        r.playCount(),
                        normalTitle,
                        normalAlbumTitle,
                        artistMap.getOrDefault(r.id(), List.of())
                );
            }).toList();

            esBulkIndexer.bulkSongs(docs);
            lastId = rows.get(rows.size() - 1).id();
            i++;
        }
        log.info("[ES_BULK_INDEXER] bulk songs = {}", i);
    }

    @Transactional(readOnly = true)
    public void reindexAlbums(int batchSize) throws IOException {
        long lastId = 0L;
        int i = 0;
        while (true) {
            List<AlbumIndexRow> rows = reindexQueryRepository.fetchAlbumRowsAfterId(lastId, batchSize);
            if (rows.isEmpty()) break;

            List<Long> ids = rows.stream()
                    .map(AlbumIndexRow::id)
                    .toList();

            Map<Long, List<String>> artistMap = reindexQueryRepository.fetchArtistNamesByAlbumIds(ids);

            List<AlbumDoc> docs = rows.stream().map(r ->{

                List<String> artistNames = artistMap.getOrDefault(r.id(), List.of());

                return new AlbumDoc(
                            r.id(),
                            r.externalId(),
                            r.title(),
                            artistMap.getOrDefault(r.id(), List.of()),
                            r.releaseDate(),
                            norm(r.title()),
                            normList(artistNames)
                    );
            }).toList();

            esBulkIndexer.bulkAlbums(docs);
            lastId = rows.get(rows.size() - 1).id();
            i++;
        }
        log.info("[ES_BULK_INDEXER] bulk albums = {}", i);
    }

    @Transactional(readOnly = true)
    public void reindexArtists(int batchSize) throws IOException {
        long lastId = 0L;
        int i=0;
        while (true) {
            List<ArtistIndexRow> rows = reindexQueryRepository.fetchArtistRowsAfterId(lastId, batchSize);
            if (rows.isEmpty()) break;

            List<ArtistDoc> docs = rows.stream().map(r -> {
                String normalName = norm(r.name());
                return new ArtistDoc(
                        r.id(),
                        r.externalId(),
                        r.name(),
                        normalName
                        );
            }).toList();

            esBulkIndexer.bulkArtists(docs);
            lastId = rows.get(rows.size() - 1).id();
            i++;
        }
        log.info("[ES_BULK_INDEXER] bulk artists = {}", i);
    }

    private String norm(String s) {
        if (s == null) return null;
        String canon = s.strip().replaceAll("\\s+", " ");
        String lowerText = canon.toLowerCase(Locale.ROOT);
        return lowerText.replaceAll("[^0-9a-z\\uAC00-\\uD7A3]+", "");
    }

    private List<String> normList(List<String> xs) {
        if (xs == null) return List.of();
        return xs.stream()
                .filter(Objects::nonNull)
                .map(this::norm)
                .filter(v -> v!=null && !v.isBlank())
                .distinct()
                .toList();
    }
}
