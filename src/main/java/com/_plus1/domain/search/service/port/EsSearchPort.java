package com._plus1.domain.search.service.port;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
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
import com._plus1.domain.search.model.dto.query.SearchQuery;
import lombok.RequiredArgsConstructor;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class EsSearchPort implements SearchEsPort {

    private static final String INDEX_SONGS = "songs";
    private static final String INDEX_ALBUMS = "albums";
    private static final String INDEX_ARTISTS = "artists";


    // 가중치용 ENUM으로 빼는 것이 좋아보임.
    private static final float BOOST_NORM_TITLE  = 500f;
    private static final float BOOST_NORM_ARTIST = 200f;
    private static final float BOOST_NORM_ALBUM  = 120f;

    private static final float BOOST_PHRASE_TITLE  = 80f;
    private static final float BOOST_PHRASE_ARTIST = 35f;
    private static final float BOOST_PHRASE_ALBUM  = 20f;

    // title.keyword
    private static final float BOOST_EXACT_RAW_TITLE = 150f;

    private final ElasticsearchClient client;


    @Override
    public Slice<SongItem> searchSongsSlice(SearchKey key) {

        boolean hasQuery = key.hasQuery();

        SearchQuery q = key.query();

        int size = key.size();
        int from = key.page() * size;


        try{
            SearchResponse<SongDoc> response = client.search(s -> {
            s.index(INDEX_SONGS);
            s.from(from);
            s.size(size + 1);
            s.trackTotalHits(t -> t.enabled(false)); // 성능용

                // payload
                sourceIncludes(s, "songId", "externalId", "title", "playCount", "releaseDate");


            s.query(root -> root.bool(b -> {
                // must : query
                if (hasQuery) {
                    b.must(m -> m.multiMatch(mm -> mm
                            .query(q.text())
                            .operator(Operator.And)
                            .fields(
                                    "title^3", "title.std",
                                    "artistNames^2", "artistNames.std",
                                    "albumTitle", "albumTitle.std"
                            )
                    ));
                    // should : raw exact
                    b.should(sh -> sh.term(t -> t
                            .field("title.keyword")
                            .value(v -> v.stringValue(q.canonical()))
                            .boost(BOOST_EXACT_RAW_TITLE)
                    ));

                    // should : phrase boost
                    b.should(sh -> sh.matchPhrase(mp -> mp.field("title")
                            .query(q.canonical())
                            .boost(BOOST_PHRASE_TITLE)));
                    b.should(sh -> sh.matchPhrase(mp -> mp.field("artistNames")
                            .query(q.canonical())
                            .boost(BOOST_PHRASE_ARTIST)));
                    b.should(sh -> sh.matchPhrase(mp -> mp.field("albumTitle")
                            .query(q.canonical())
                            .boost(BOOST_PHRASE_ALBUM)));

                    // should : norm exact boost
                    b.should(sh -> sh.term(t -> t.field("titleNorm")
                            .value(v -> v.stringValue(q.norm()))
                            .boost(BOOST_NORM_TITLE)));
                    b.should(sh -> sh.term(t -> t.field("artistNamesNorm")
                            .value(v -> v.stringValue(q.norm()))
                            .boost(BOOST_NORM_ARTIST)));
                    b.should(sh -> sh.term(t -> t.field("albumTitleNorm")
                            .value(v -> v.stringValue(q.norm()))
                            .boost(BOOST_NORM_ALBUM)));
                } else {
                    b.must(m -> m.matchAll(ma -> ma));
                }

                applyReleaseDateRangeFilter(b, key);
                return b;
            }));

                // q 있으면 score 우선 : 부스트가 실제 상단에 반영.
                scoreFirstIfQuery(s, hasQuery);

                // 정렬 : TieBreaker
                if (key.sort() == SearchSort.POPULAR) {
                    s.sort(so -> so.field(f -> f.field("playCount")
                            .order(SortOrder.Desc)));
                    s.sort(so -> so.field(f -> f.field("releaseDate")
                            .order(SortOrder.Desc)
                            .missing("_last")));
                } else {
                    s.sort(so -> so.field(f -> f.field("releaseDate").order(SortOrder.Desc)
                            .missing("_last")));
                }
                s.sort(so -> so.field(f -> f.field("songId")
                        .order(SortOrder.Desc)));

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

        boolean hasQuery = key.hasQuery();
        SearchQuery q = key.query();

        try {
            SearchResponse<AlbumDoc> response = client.search(s -> {
                s.index(INDEX_ALBUMS);
                s.from(from);
                s.size(size + 1); // slice
                s.trackTotalHits(t -> t.enabled(false));

                // payload 줄이기
                sourceIncludes(s,"albumId", "externalId", "title", "releaseDate");

                s.query(root -> root.bool(b -> {
                    if (hasQuery) {
                        // must: 토큰 AND (OR 착시 제거)
                        b.must(m -> m.multiMatch(mm -> mm
                                .query(q.text())
                                .operator(Operator.And)
                                .fields("title^3", "title.std", "artistNames^2", "artistNames.std")
                        ));

                        // should: phrase boost
                        b.should(sh -> sh.matchPhrase(mp -> mp.field("title")
                                .query(q.canonical())
                                .boost(80f)
                        ));
                        b.should(sh -> sh.matchPhrase(mp -> mp.field("artistNames")
                                .query(q.canonical())
                                .boost(35f)
                        ));

                        // should : norm exact boost : 매핑 및 리인덱스 되어있을 때.
                        b.should(sh -> sh.term(t -> t.field("titleNorm")
                                .value(v -> v.stringValue(q.norm()))
                                .boost(500f)
                        ));
                        b.should(sh -> sh.term(t -> t.field("artistNamesNorm")
                                .value(v -> v.stringValue(q.norm()))
                                .boost(200f)
                        ));

                        // raw exact: title.keyword가 있을 때.
                        b.should(sh -> sh.term(t -> t.field("title.keyword")
                                .value(v -> v.stringValue(q.canonical()))
                                .boost(120f)
                        ));
                    } else {
                        b.must(m -> m.matchAll(ma -> ma));
                    }

                    // releaseDate range filter
                    applyReleaseDateRangeFilter(b, key);
                    return b;
                }));

                // q 있으면 score 우선 : 부스트가 실제 상단에 반영.
                scoreFirstIfQuery(s, hasQuery);

                // 정렬 : tieBreaker
                if (key.sort() == SearchSort.LATEST) {
                    s.sort(so -> so.field(f -> f.field("releaseDate")
                            .order(SortOrder.Desc)
                            .missing("_last")));
                } else {
                    // POPULAR : 임시로 albumId로 통일.
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

            boolean hasNext = docs.size() > size;
            if (hasNext) docs = docs.subList(0, size);

            List<AlbumItem> rows = docs.stream()
                    .map(d -> new AlbumItem(
                            d.albumId(),
                            d.externalId(),
                            d.title(),
                            d.releaseDate()
                    ))
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
        int from = key.page() * size;

        boolean hasQuery = key.hasQuery();
        SearchQuery q = key.query();

        try {
            SearchResponse<ArtistDoc> response = client.search(s -> {
                s.index(INDEX_ARTISTS);
                s.from(from);
                s.size(size + 1);
                s.trackTotalHits(t -> t.enabled(false));

                // payload 줄이기
                sourceIncludes(s,"artistId", "externalId", "name");

                s.query(root -> root.bool(b -> {
                    if (hasQuery) {
                        // AND
                        b.must(m -> m.multiMatch(mm -> mm
                                .query(q.text())
                                .operator(Operator.And)
                                .fields("name^3", "name.std")
                        ));

                        // should : phrase boost
                        b.should(sh -> sh.matchPhrase(mp -> mp.field("name")
                                .query(q.canonical())
                                .boost(80f)
                        ));

                        // should : norm exact boost
                        b.should(sh -> sh.term(t -> t.field("nameNorm")
                                .value(v -> v.stringValue(q.norm()))
                                .boost(500f)
                        ));

                        // raw exact: name.keyword
                        b.should(sh -> sh.term(t -> t.field("name.keyword")
                                .value(v -> v.stringValue(q.canonical()))
                                .boost(120f)
                        ));
                    } else {
                        b.must(m -> m.matchAll(ma -> ma));
                    }

                    // artist -> releaseDate 없음.
                    return b;
                }));

                // q 있으면 score 우선
                scoreFirstIfQuery(s, hasQuery);

                // 기존 tie-break 유지
                s.sort(so -> so.field(f -> f.field("artistId").order(SortOrder.Desc)));
                return s;
                }, ArtistDoc.class);

            List<ArtistDoc> docs = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

            boolean hasNext = docs.size() > size;
            if (hasNext) docs = docs.subList(0, size);

            List<ArtistItem> rows = docs.stream()
                    .map(d -> new ArtistItem(
                            d.artistId(),
                            d.externalId(),
                            d.name()))
                    .toList();

            Pageable pageable = PageRequest.of(key.page(), key.size());
            return new SliceImpl<>(rows, pageable, hasNext);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 발매일 가중치.
    private void applyReleaseDateRangeFilter(BoolQuery.Builder boolQuery, SearchKey key){
        if (key.from() != null || key.to() != null) {
            boolQuery.filter(f -> f.range(r -> {
                r.field("releaseDate");
                if (key.from() != null) r.gte(JsonData.of(key.from().toString()));
                if (key.to() != null) r.lte(JsonData.of(key.to().toString()));
                return r;
            }));
        }
    }

    // 정렬 가중치
    private void scoreFirstIfQuery(SearchRequest.Builder request, boolean hasQuery) {
        if (hasQuery) {
            request.sort(so -> so.score(sc -> sc.order(SortOrder.Desc)));
        }
    }

    // payload 줄이기
    private void sourceIncludes(SearchRequest.Builder request, String... includes) {
        request.source(src -> src.filter(f -> f.includes(
                Arrays.asList(includes))));
    }
}
