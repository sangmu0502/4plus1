package com._plus1.domain.search.service;


import com._plus1.common.entity.PopularSearch;
import com._plus1.domain.search.model.dto.PopularKeywordDto;
import com._plus1.domain.search.repository.PopularSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularSearchService {

    @Value("${popular.record.enabled:true}")
    private boolean recordEnabled;
    private final PopularSearchRepository popularSearchRepository;

    // 1. /popular?limit=10 : DB에서 Top N : 뽑아서 Return
    // 같은 limit로 요청 반복 : 캐시에서 응답.
    @Cacheable(cacheNames = "popularKeywords", key = "'limit=' + #limit")
    @Transactional
    public List<PopularKeywordDto> getTop(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<PopularSearch> items = popularSearchRepository.findTop(pageable);

        return PopularKeywordDto.from(items);
    }


    @CacheEvict(cacheNames = "popularKeywords", allEntries = true)
    @Transactional
    public void record(String keyword) {
        if (!recordEnabled) return;
        if (keyword == null || keyword.isBlank()) return;
        popularSearchRepository.upsertIncrement(keyword.trim());
    }

}