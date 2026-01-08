package com._plus1.domain.search.service;



import com._plus1.domain.search.model.dto.cache.SearchKey;
import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.model.dto.response.SearchSliceResponse;
import com._plus1.domain.search.repository.SearchQueryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

// 0. Response
@Service
@RequiredArgsConstructor
public class SearchCachedService {

    private final SearchQueryRepository searchQueryRepository;

    // 1. Page
    @Cacheable(cacheNames="searchCached:paged",
            key="#condition.toCache()",
            condition="#condition.cacheable()", sync = true)
    @Transactional(readOnly = true)
    public SearchResponse searchCached(SearchKey condition) {
        return new SearchResponse(
                condition.query().norm(),
                searchQueryRepository.searchSongs(condition),
                searchQueryRepository.searchAlbums(condition),
                searchQueryRepository.searchArtists(condition)
        );
    }

    // 2. Slice
    @Cacheable(cacheNames="searchCached:sliced",
            key="#condition.toCache()",
            condition="#condition.cacheable()", sync = true)
    @Transactional(readOnly = true)
    public SearchSliceResponse searchCachedSlice(SearchKey condition) {
        return new SearchSliceResponse(
                condition.query().norm(),
                searchQueryRepository.searchSongsSlice(condition),
                searchQueryRepository.searchAlbumsSlice(condition),
                searchQueryRepository.searchArtistsSlice(condition)
        );
    }

}