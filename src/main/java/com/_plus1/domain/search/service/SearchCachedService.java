package com._plus1.domain.search.service;

import com._plus1.domain.search.model.dto.SearchSort;

import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.repository.SearchQueryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

// 0. Response
@Service
@RequiredArgsConstructor
public class SearchCachedService {

    private final SearchQueryRepository searchQueryRepository;
    private final SearchCacheReadService cacheReadService;

    // 1. NoCache
    public SearchResponse searchNoCache(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        return new SearchResponse(
                q,
                searchQueryRepository.searchSongs(q, from, to, sort, pageable),
                searchQueryRepository.searchAlbums(q, from, to, sort, pageable),
                searchQueryRepository.searchArtists(q, sort, pageable)
        );
    }

    // 1. Cache
    public SearchResponse searchCached(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        return new SearchResponse(
                q,
                cacheReadService.songs(q, from, to, sort, pageable),
                cacheReadService.albums(q, from, to, sort, pageable),
                cacheReadService.artists(q, sort, pageable)
        );
    }
}