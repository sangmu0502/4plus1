package com._plus1.domain.search.integration;


import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.repository.SearchQueryRepository;
import com._plus1.domain.search.service.PopularSearchService;
import com._plus1.domain.search.service.SearchCacheEvictService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchCacheIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired CacheManager cacheManager;

    @MockitoBean SearchQueryRepository searchQueryRepository;
    @MockitoBean PopularSearchService popularSearchService;
    @Autowired
    private SearchCacheEvictService searchCacheEvictService;

    @BeforeEach
    void setUp(){
        // 1. 인기 검색어 기록 제외
        doNothing().when(popularSearchService).record(anyString());

        // 2. repository : nullableMatcher 사용
        // 1). songs
        when(searchQueryRepository.searchSongs(
                anyString(), // query
                nullable(LocalDate.class), // from
                nullable(LocalDate.class), // to
                any(SearchSort.class), // sort
                any(Pageable.class) // pageable
                )).thenReturn(Page.empty());

        // 2). albums
        when(searchQueryRepository.searchAlbums(
                anyString(),
                nullable(LocalDate.class),
                nullable(LocalDate.class),
                any(SearchSort.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        // 3). artists
        when(searchQueryRepository.searchArtists(
                anyString(),
                any(SearchSort.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        searchCacheEvictService.clearAll();
    }

    @AfterEach
    void tearDown() {
        searchCacheEvictService.clearAll();
    }

    private void knock(String q, String sort, String page, String size) throws Exception{
        mockMvc.perform(get("/api/search/ver2")
                        .param("q",q)
                        .param("sort", sort)
                        .param("page", page)
                        .param("size", size))
                .andExpect(status().isOk());
    }

    private void knockVerOne(String q, String sort, String page, String size) throws Exception{
        mockMvc.perform(get("/api/search")
                        .param("q",q)
                        .param("sort", sort)
                        .param("page", page)
                        .param("size", size))
                .andExpect(status().isOk());
    }


    private void verifySearch(String q, int i){
        verify(searchQueryRepository, times(i))
                .searchSongs(
                        eq(q),
                        isNull(),
                        isNull(),
                        eq(SearchSort.LATEST),
                        any(Pageable.class)
                );
        verify(searchQueryRepository, times(i))
                .searchAlbums(
                        eq(q),
                        isNull(),
                        isNull(),
                        eq(SearchSort.LATEST),
                        any(Pageable.class)
                );
        verify(searchQueryRepository, times(i))
                .searchArtists(
                        eq(q),
                        eq(SearchSort.LATEST),
                        any(Pageable.class)
                );
    }

    // 3. 같은 요청 2번에 Repository 1회 호출.
    @Test
    void ver2_cacheHit_sameRequest_shouldCallRepositoryOnce()throws Exception{
        knock("hello",  "LATEST", "0", "50");
        knock("hello",  "LATEST", "0", "50");

        verifySearch("hello",1);

        verify(popularSearchService, times(2)).record(eq("hello"));
    }

    // 4. 쿼리 다른 경우 : Repository 2회 호출.
    @Test
    void ver2_cacheMiss_queryDifferent_shouldCallRepositoryTwice()throws Exception{
        knock("hello",  "LATEST", "0", "50");
        knock("world",  "LATEST", "0", "50");

        verifySearch("hello", 1);
        verifySearch("world", 1);
    }

    // 5. 페이지 다른 경우, Repository 2회 호출.
    @Test
    void ver2_cacheMiss_pageDifferent_shouldCallRepositoryTwice()throws Exception{
        knock("hello",  "LATEST", "0", "50");
        knock("hello",  "LATEST", "42", "50");

        verifySearch("hello",2);
    }

    // 6. ver 1 : NoCache : 같은 요청 2회, Repository 2회 호출.
    @Test
    void ver1_noCache_sameRequest_shouldCallRepositoryTwice() throws Exception{
        knockVerOne("hello", "LATEST", "0", "50");
        knockVerOne("hello",  "LATEST", "0", "50");

        verifySearch("hello",2);
    }
}
