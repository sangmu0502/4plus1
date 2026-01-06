package com._plus1.domain.search.integration;


import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.cache.SearchKey;
import com._plus1.domain.search.repository.SearchQueryRepository;
import com._plus1.domain.search.service.PopularSearchService;
import com._plus1.domain.search.service.SearchCacheEvictService;
import com._plus1.domain.search.service.SearchCachedService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SearchCachedServiceTest {
    @Autowired SearchCachedService searchCachedService;

    @Autowired SearchCacheEvictService searchCacheEvictService;

    @MockitoBean SearchQueryRepository searchQueryRepository;
    @MockitoBean PopularSearchService popularSearchService;

    @Autowired Environment env;

    // 환경변수 체크
    @Test
    void profileCheck() {
        assertThat(env.getActiveProfiles()).contains("test");
    }

    @BeforeEach
    void setUp(){
        // 1. 인기 검색어 기록 제외
        doNothing().when(popularSearchService).record(anyString());

        // 2. repository : nullableMatcher 사용
        // 1). songs
        when(searchQueryRepository.searchSongs(any(SearchKey.class))).thenReturn(Page.empty());

        // 2). albums
        when(searchQueryRepository.searchAlbums(any(SearchKey.class))).thenReturn(Page.empty());

        // 3). artists
        when(searchQueryRepository.searchArtists(any(SearchKey.class))).thenReturn(Page.empty());

        // 4). songs : Slice
        when(searchQueryRepository.searchSongsSlice(any(SearchKey.class))).thenReturn(
                new SliceImpl<>(List.of(), PageRequest.of(0, 50), false));

        // 5). albums : Slice
        when(searchQueryRepository.searchAlbumsSlice(any(SearchKey.class))).thenReturn(
                new SliceImpl<>(List.of(), PageRequest.of(0, 50), false));

        // 6). artists : Slice
        when(searchQueryRepository.searchArtistsSlice(any(SearchKey.class))).thenReturn(
                new SliceImpl<>(List.of(), PageRequest.of(0, 50), false));

        searchCacheEvictService.clearAll();
    }

    @AfterEach
    void tearDown() {
        searchCacheEvictService.clearAll();
    }

    // - Page - //

    // 3. 같은 요청 2번에 Repository 1회 호출.
    @Test
    void paged_cacheHit_sameRequest_shouldCallRepositoryOnce()throws Exception{
        SearchKey key = new SearchKey("hello", null, null, SearchSort.LATEST, 0, 50);

        searchCachedService.searchCached(key);
        searchCachedService.searchCached(key);

        verify(searchQueryRepository, times(1)).searchSongs(any(SearchKey.class));
        verify(searchQueryRepository, times(1)).searchAlbums(any(SearchKey.class));
        verify(searchQueryRepository, times(1)).searchArtists(any(SearchKey.class));
    }

    // 4. 쿼리 다른 경우 : Repository 2회 호출.
    @Test
    void paged_cacheMiss_queryDifferent_shouldCallRepositoryTwice()throws Exception{
        SearchKey keyOne = new SearchKey("hello", null, null, SearchSort.LATEST, 0, 50);
        SearchKey keyTwo = new SearchKey("world", null, null, SearchSort.LATEST, 0, 50);

        searchCachedService.searchCached(keyOne);
        searchCachedService.searchCached(keyTwo);

        verify(searchQueryRepository, times(2)).searchSongs(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchAlbums(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchArtists(any(SearchKey.class));
    }

    // 5. 페이지 != 0 -> noCache
    @Test
    void paged_cacheMiss_pageDifferent_shouldCallRepositoryTwice()throws Exception{
        SearchKey key = new SearchKey("hello", null, null, SearchSort.LATEST, 1, 50);

        searchCachedService.searchCached(key);
        searchCachedService.searchCached(key);

        verify(searchQueryRepository, times(2)).searchSongs(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchAlbums(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchArtists(any(SearchKey.class));
    }

    // 6. TTL
    @Test
    void paged_ttlExpire_shouldCallRepositoryTwice() throws Exception{
        // 1). Repository : 1회
        SearchKey key = new SearchKey("hello", null, null, SearchSort.LATEST, 0, 50);

        // 2). knock
        searchCachedService.searchCached(key);

        // 3). TTL
        Thread.sleep(350);

        // 4). knock
        searchCachedService.searchCached(key);

        // 5). Repository : 2회
        verify(searchQueryRepository, times(2)).searchSongs(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchAlbums(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchArtists(any(SearchKey.class));
    }

    // - Slice - //
    // 7. 2회 캐시 호출 -> 1회 Repository 응답
    @Test
    void sliced_cacheHit_sameKey_shouldCallRepositoryOnce() {
        SearchKey key = new SearchKey("hello", null, null, SearchSort.LATEST, 0, 50);

        searchCachedService.searchCachedSlice(key);
        searchCachedService.searchCachedSlice(key);

        verify(searchQueryRepository, times(1)).searchSongsSlice(any(SearchKey.class));
        verify(searchQueryRepository, times(1)).searchAlbumsSlice(any(SearchKey.class));
        verify(searchQueryRepository, times(1)).searchArtistsSlice(any(SearchKey.class));
    }

    // 8. NotCacheable
    @Test
    void sliced_cacheBypassed_notCacheable_shouldCallRepositoryTwice() {
        SearchKey key = new SearchKey("hello", null, null, SearchSort.LATEST, 1, 50);

        searchCachedService.searchCachedSlice(key);
        searchCachedService.searchCachedSlice(key);

        verify(searchQueryRepository, times(2)).searchSongsSlice(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchAlbumsSlice(any(SearchKey.class));
        verify(searchQueryRepository, times(2)).searchArtistsSlice(any(SearchKey.class));
    }
}
