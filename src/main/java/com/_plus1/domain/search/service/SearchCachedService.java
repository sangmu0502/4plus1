package com._plus1.domain.search.service;


import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.repository.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SearchCachedService {
    private final SearchQueryRepository searchQueryRepository;

    @Transactional(readOnly=true)
    public SearchResponse searchNoCache(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        return doSearch(q, from, to, sort, pageable);
    }

    // SpEL
    @Cacheable(cacheNames = "search:v2")
    @Transactional(readOnly=true)
    public SearchResponse searchCached(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        return doSearch(q, from, to, sort, pageable);
    }

    private SearchResponse doSearch(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        Page<SongItem> songs = searchQueryRepository.searchSongs(q, from, to, sort, pageable);
        Page<AlbumItem> albums = searchQueryRepository.searchAlbums(q, from, to, sort,  pageable);
        Page<ArtistItem> artists = searchQueryRepository.searchArtists(q, sort, pageable);

        return new SearchResponse(
                q,
                songs,
                albums,
                artists
        );
    }


}
