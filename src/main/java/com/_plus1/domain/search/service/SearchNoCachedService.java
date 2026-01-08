package com._plus1.domain.search.service;

import com._plus1.domain.search.model.dto.cache.SearchKey;

import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.model.dto.response.SearchSliceResponse;

import com._plus1.domain.search.repository.SearchQueryRepository;
import com._plus1.domain.search.service.port.SearchEsPort;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 0. Response
@Service
@RequiredArgsConstructor
public class SearchNoCachedService {

    private final SearchQueryRepository searchQueryRepository;
    private final SearchEsPort searchEsPort;


    // 1. Page
    @Transactional(readOnly = true)
    public SearchResponse searchNoCache(SearchKey condition) {
        return new SearchResponse(
                condition.query().norm(),
                searchQueryRepository.searchSongs(condition),
                searchQueryRepository.searchAlbums(condition),
                searchQueryRepository.searchArtists(condition)
        );
    }

    // 2. Slice
    @Transactional(readOnly = true)
    public SearchSliceResponse searchSliceNoCache(SearchKey condition) {
        return new SearchSliceResponse(
                condition.query().norm(),
                searchQueryRepository.searchSongsSlice(condition),
                searchQueryRepository.searchAlbumsSlice(condition),
                searchQueryRepository.searchArtistsSlice(condition)
        );
    }

    // 3. ES
    @Transactional(readOnly = true)
    public SearchSliceResponse searchEs(SearchKey condition) {
        return new SearchSliceResponse(
                condition.query().norm(),
                searchEsPort.searchSongsSlice(condition),
                searchEsPort.searchAlbumsSlice(condition),
                searchEsPort.searchArtistsSlice(condition)
        );
    }
}
