package com._plus1.domain.search.service;

import com._plus1.domain.search.component.SearchKeyFactory;
import com._plus1.domain.search.model.dto.PopularKeywordDto;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.cache.SearchKey;
import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.model.dto.response.SearchSliceResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SearchService {

    private final PopularSearchService popularSearchService;
    private final SearchCachedService searchCachedService;
    private final SearchNoCachedService searchNoCachedService;
    private final SearchKeyFactory searchKeyFactory;

    // KEYWORD FROM TO SORT
    // 1. NoCache
    @Transactional
    public SearchResponse search(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 정규화 + 객체
        SearchKey condition = searchKeyFactory.create(query, from, to, sort, rawPage, rawSize);

        // 2. 인기 검색어 기록
        popularSearchService.record(condition.q());

        // 3. return
        return searchNoCachedService.searchNoCache(condition);
    }

    // 2. Cache

    public SearchResponse searchVersionTwo(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 정규화 + 객체
        SearchKey condition = searchKeyFactory.create(query, from, to, sort, rawPage, rawSize);

        // 2. 인기 검색어 기록
        popularSearchService.record(condition.q());

        // 3. return
        return searchCachedService.searchCached(condition);
    }

    // 3. Slice
    public SearchSliceResponse searchVersionThree(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 정규화 + 객체
        SearchKey condition = searchKeyFactory.create(query, from, to, sort, rawPage, rawSize);

        // 2. 인기 검색어 기록
        popularSearchService.record(condition.q());

        // 3. return
        return searchNoCachedService.searchSliceNoCache(condition);
    }

    // 4. Slice
    public SearchSliceResponse searchVersionFour(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 정규화 + 객체
        SearchKey condition = searchKeyFactory.create(query, from, to, sort, rawPage, rawSize);

        // 2. 인기 검색어 기록
        popularSearchService.record(condition.q());

        // 3. return
        return searchCachedService.searchCachedSlice(condition);
    }

    // 5. ES
    public SearchSliceResponse searchVersionFive(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 정규화 + 객체
        SearchKey condition = searchKeyFactory.create(query, from, to, sort, rawPage, rawSize);

        // 2. 인기 검색어 기록
        popularSearchService.record(condition.q());

        // 3. return
        return searchNoCachedService.searchEs(condition);
    }

    // 기타 : popular
    public List<PopularKeywordDto> popular(int limit) {
        int n = Math.min(Math.max(limit, 1), 50);
        return popularSearchService.getTop(n);
    }
}
