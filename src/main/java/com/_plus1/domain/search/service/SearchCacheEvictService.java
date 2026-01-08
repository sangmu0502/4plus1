package com._plus1.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCacheEvictService {

    @CacheEvict(cacheNames={"searchCached:paged", "searchCached:sliced"}, allEntries=true)
    public void clearAll(){}
}
