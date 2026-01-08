package com._plus1.domain.search.model.dto.cache;

import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.query.SearchQuery;

import java.time.LocalDate;

public record SearchKey(
        SearchQuery query,
        LocalDate from,
        LocalDate to,
        SearchSort sort,
        int page,
        int size
) {
    public boolean hasQuery() {
        return query != null && !query.isEmpty();
    }

    // 캐시 조건 : page 0, query Not Null, query의 길이 2자 이상, size 50 이하.
    public boolean cacheable(){
        return page == 0
                && hasQuery()
                && query.norm() != null
                && query.norm().length() >= 2
                && size <= 50;
    }

    // 캐시 키 변형 : norm 기준.
    public String toCache(){
        // Query Check
        String q = hasQuery() ?
                query.norm() : "";

        return "q=" + q
                + "|from=" + (from == null ? "" : from)
                + "|to=" + (to == null ? "" : to)
                + "|sort=" + (sort == null ? "LATEST" : sort.name())
                + "|page=" + page
                + "|size=" + size;
    }
}
