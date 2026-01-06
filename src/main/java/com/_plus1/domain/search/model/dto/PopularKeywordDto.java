package com._plus1.domain.search.model.dto;

import com._plus1.common.entity.PopularSearch;

import java.util.List;

public record PopularKeywordDto(String keyword, long count) {
    public static List<PopularKeywordDto> from(List<PopularSearch> items) {
        return items.stream().
                map(item->new PopularKeywordDto(item.getKeyword(), item.getCount()))
                .toList();
    }
}
