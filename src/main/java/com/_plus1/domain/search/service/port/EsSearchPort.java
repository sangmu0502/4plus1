package com._plus1.domain.search.service.port;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.cache.SearchKey;
import com._plus1.domain.search.model.dto.docs.AlbumDoc;
import com._plus1.domain.search.model.dto.docs.ArtistDoc;
import com._plus1.domain.search.model.dto.docs.SongDoc;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository("esSongSearchPort")
@RequiredArgsConstructor
public class EsSearchPort implements SearchEsPort {

    private static final String INDEX_SONGS = "songs";
    private static final String INDEX_ALBUMS = "albums";
    private static final String INDEX_ARTISTS = "artists";
    private final ElasticsearchClient client;

    @Override
    public Slice<SongItem> searchSongsSlice(SearchKey key) {
        int size = key.size();
        int from = key.page() * size;
        try{
            SearchResponse<SongDoc> response = client.search(s -> {
            s.index(INDEX_SONGS);
            s.from(from);
            s.size(size + 1);
            s.trackTotalHits(t -> t.enabled(false)); // 성능용

            s.query(q -> q.bool(b -> {
                // must : query
                if (key.q() != null && !key.q().isBlank()) {
                    b.must(m -> m.multiMatch(mm -> mm
                            .query(key.q())
                            .fields(
                                    "title^3", "title.std",
                                    "artistNames^2", "artistNames.std",
                                    "albumTitle", "albumTitle.std"
                            )
                    ));
                } else {
                    b.must(m -> m.matchAll(ma -> ma));
                }

                // 정렬 필터 : releaseDate range
                if (key.from() != null || key.to() != null) {
                    b.filter(f -> f.range(r -> {
                        r.field("releaseDate");
                        if (key.from() != null) r.gte(JsonData.of(key.from().toString()));
                        if (key.to() != null) r.lte(JsonData.of(key.to().toString()));
                        return r;
                    }));
                }
                return b;
            }));

            // sort
            if (key.sort() == SearchSort.POPULAR) {
                s.sort(so -> so.field(f -> f.field("playCount")
                        .order(SortOrder.Desc)));
                s.sort(so -> so.field(f -> f.field("releaseDate")
                        .order(SortOrder.Desc).missing("_last")));
            } else {
                s.sort(so -> so.field(f -> f.field("releaseDate")
                        .order(SortOrder.Desc).missing("_last")));
            }
            s.sort(so -> so.field(f -> f.field("songId").order(SortOrder.Desc))); // songId로 정렬 tie-break

            return s;
            }, SongDoc.class);

            List<SongDoc> docs = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

            // slice
            boolean hasNext = docs.size() > size;
            if (hasNext) docs = docs.subList(0, size);

            List<SongItem> rows = docs.stream()
                    .map(d -> new SongItem(
                            d.songId(),
                            d.externalId(),
                            d.title(),
                            d.playCount() == null ? 0L : d.playCount(),
                            d.releaseDate()
                    ))
                    .toList();

            Pageable pageable = PageRequest.of(key.page(), key.size());
            return new SliceImpl<>(rows, pageable, hasNext);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Slice<AlbumItem> searchAlbumsSlice(SearchKey key) {
        int size = key.size();
        int from = key.page() * size;

        try{
            SearchResponse<AlbumDoc> response = client.search(s -> {
                s.index(INDEX_ALBUMS);
                s.from(from);
                s.size(size + 1); // slice
                s.trackTotalHits(t -> t.enabled(false));
                s.query(q -> q.bool(b -> {
                    if (key.q() != null && !key.q().isBlank()) {
                        b.must(m -> m.multiMatch(mm -> mm
                                .query(key.q())
                                .fields("title^3", "title.std", "artistNames^2", "artistNames.std")
                        ));
                    } else b.must(m -> m.matchAll(ma -> ma));

                    if (key.from() != null || key.to() != null) {
                        b.filter(f -> f.range(r -> {
                            r.field("releaseDate");
                            if (key.from() != null) r.gte(JsonData.of(key.from().toString()));
                            if (key.to() != null) r.lte(JsonData.of(key.to().toString()));
                            return r;
                        }));
                    }
                    return b;
                }));

                if (key.sort() == SearchSort.LATEST) {
                    s.sort(so -> so.field(f -> f.field("releaseDate")
                            .order(SortOrder.Desc)
                            .missing("_last")));
                } else {
                    // POPULAR : albumId desc로 일단 통일.
                    s.sort(so -> so.field(f -> f.field("albumId")
                            .order(SortOrder.Desc)));
                }
                s.sort(so -> so.field(f -> f.field("albumId")
                        .order(SortOrder.Desc)));

                return s;
            }, AlbumDoc.class);

            List<AlbumDoc> docs = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

            // Slice
            boolean hasNext = docs.size() > size;
            if (hasNext) docs = docs.subList(0, size);

            List<AlbumItem> rows = docs.stream()
                    .map(d -> new AlbumItem(
                            d.albumId(),
                            d.externalId(),
                            d.title(),
                            d.releaseDate()))
                    .toList();

            Pageable pageable = PageRequest.of(key.page(), key.size());

            return new SliceImpl<>(rows, pageable, hasNext);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Slice<ArtistItem> searchArtistsSlice(SearchKey key) {
        int size = key.size();
        int from = key.page() * size; // index
        try{
            SearchResponse<ArtistDoc> response = client.search(s -> {
                s.index(INDEX_ARTISTS);
                s.from(from);
                s.size(size + 1);
                s.trackTotalHits(t -> t.enabled(false));

                s.query(q -> {
                    if (key.q() != null && !key.q().isBlank()) {
                        return q.multiMatch(mm -> mm.query(key.q()).fields("name^3", "name.std"));
                    }
                    return q.matchAll(ma -> ma);
                });

                s.sort(so -> so.field(f -> f.field("artistId").order(SortOrder.Desc)));

                return s;
            }, ArtistDoc.class);

            List<ArtistDoc> docs = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

            // Slice
            boolean hasNext = docs.size() > size;
            if (hasNext) docs = docs.subList(0, size);

            List<ArtistItem> rows = docs.stream()
                    .map(d -> new ArtistItem(d.artistId(), d.externalId(), d.name()))
                    .toList();

            Pageable pageable = PageRequest.of(key.page(), key.size());

            return new SliceImpl<>(rows, pageable, hasNext);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


}
