package com._plus1.domain.search.service.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com._plus1.domain.search.model.dto.docs.AlbumDoc;
import com._plus1.domain.search.model.dto.docs.ArtistDoc;
import com._plus1.domain.search.model.dto.docs.SongDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsBulkIndexer {

    private final ElasticsearchClient client;

    // 1. Song 적재
    public void bulkSongs(List<SongDoc> docs) throws IOException {

        BulkRequest.Builder bulkReq = new BulkRequest.Builder();
        for (SongDoc doc : docs) {
            bulkReq.operations(op -> op
                    .index(idx -> idx
                            .index("songs")
                            .id(String.valueOf(doc.songId()))
                            .document(doc)
                    )
            );
        }
        BulkResponse bulkRes = client.bulk(bulkReq.build());
        if (bulkRes.errors()) {
            throw new IllegalStateException("songs bulk has errors");
        }

    }

    // 2. Albums 적재
    public void bulkAlbums(List<AlbumDoc> docs) throws IOException {

        BulkRequest.Builder bulkReq = new BulkRequest.Builder();
        for (AlbumDoc doc : docs) {
            bulkReq.operations(op -> op
                    .index(idx -> idx
                            .index("albums")
                            .id(String.valueOf(doc.albumId()))
                            .document(doc)
                    )
            );
        }
        BulkResponse bulkRes = client.bulk(bulkReq.build());
        if (bulkRes.errors()) {
            throw new IllegalStateException("albums bulk has errors");
        }
    }

    // 3. Artists 적재
    public void bulkArtists(List<ArtistDoc> docs) throws IOException {

        BulkRequest.Builder bulkReq = new BulkRequest.Builder();
        for (ArtistDoc doc : docs) {
            bulkReq.operations(op -> op
                    .index(idx -> idx
                            .index("artists")
                            .id(String.valueOf(doc.artistId()))
                            .document(doc)
                    )
            );

        }
        BulkResponse bulkRes = client.bulk(bulkReq.build());
        if (bulkRes.errors()) {
            throw new IllegalStateException("artists bulk has errors");
        }
    }
}