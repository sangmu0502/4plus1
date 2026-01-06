package com._plus1.domain.search.component;

import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.cache.SearchKey;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;

@Component
public class SearchKeyFactory{

    private static final int DEFAULT_PAGE=0;
    private static final int DEFAULT_PAGE_SIZE=50;
    private static final int MAX_PAGE_SIZE=100;


    public SearchKey create(
            String rawQuery,
            LocalDate from,
            LocalDate to,
            SearchSort sort,
            Integer rawPage,
            Integer rawSize
    ){
        String query = normalizeQuery(rawQuery);

        SearchSort safeSort = (sort == null) ? SearchSort.LATEST : sort;

        int page = normalizePage(rawPage);
        int size = normalizeSize(rawSize);

        validateRange(from, to);

        return new SearchKey(query, from, to, safeSort, page, size);
    }

    // 1. Query Normalize
    private String normalizeQuery(String rawQuery){
        if(rawQuery == null || rawQuery.isBlank()){
            throw new CustomException(ErrorCode.EMPTY_QUERY);
        }
        // token
        return rawQuery.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    // 2. Page Normalize
    private int normalizePage(Integer rawPage){
        if(rawPage == null) return DEFAULT_PAGE;
        return Math.max(rawPage, DEFAULT_PAGE);
    }

    // 3. Size Normalize
    private int normalizeSize(Integer rawSize){
        if(rawSize == null || rawSize <=0) return DEFAULT_PAGE_SIZE;
        return Math.min(rawSize, MAX_PAGE_SIZE);
    }

    // 4. range : from ~ to
    private void validateRange(LocalDate from, LocalDate to){
        if(from!=null && to != null && from.isAfter(to)){
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}