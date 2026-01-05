package com._plus1.domain.search.service;

import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.search.model.dto.PopularKeywordDto;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.repository.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchQueryRepository searchQueryRepository;
    private final PopularSearchService popularSearchService;
    private final SearchCachedService searchCachedService;

    private static final int DEFAULT_PAGE=0;
    private static final int DEFAULT_PAGE_SIZE=50;
    private static final int MAX_PAGE_SIZE=100;

    // KEYWORD FROM TO SORT
    // Upsert : popularSearchService.record(q);
    public SearchResponse search(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 쿼리 정규화
        String q = normalizeQuery(query);

        // 2. 페이지 정규화
        Pageable pageable = of(rawPage, rawSize);

        // 3. 인기 검색어 기록
        popularSearchService.record(q);

        // 4. return
        return searchCachedService.searchNoCache(q, from, to, sort, pageable);
    }


    public SearchResponse searchVersionTwo(
            String query,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ) {
        // 1. 쿼리 정규화
        String q = normalizeQuery(query);

        // 2. 페이지 정규화
        Pageable pageable = of(rawPage, rawSize);

        // 3. 인기 검색어 기록
        popularSearchService.record(q);

        // 4. return
        return searchCachedService.searchCached(q, from, to, sort, pageable);
    }

    public List<PopularKeywordDto> popular(int limit) {
        int n = Math.min(Math.max(limit, 1), 50);
        return popularSearchService.getTop(n);
    }


    private String normalizeQuery(String query){
        if(query==null||query.isBlank()){
            throw new CustomException(ErrorCode.EMPTY_QUERY);
        }

        return query.trim().toLowerCase(Locale.ROOT);
    }



    private Pageable of(Integer rawPage, Integer rawPageSize){
        // 1. rawPage가 null, true : DEFAULT_PAGE=0(0Based) false : rawPage DEFAULT_PAGE 중 큰 값
        // 1-1). page 음수 방지.
        int p=(rawPage==null)
                ? DEFAULT_PAGE
                : Math.max(rawPage, DEFAULT_PAGE);


        // 2. rawPageSize null, 음수 방지.
        int s;
        if(rawPageSize==null||rawPageSize<=0){
            s=DEFAULT_PAGE_SIZE;
        }else{
            s=Math.min(rawPageSize, MAX_PAGE_SIZE);
        }
        return PageRequest.of(p,s);
    }
}
