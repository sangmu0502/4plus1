package com._plus1.domain.search.service;

import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import com._plus1.domain.search.repository.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// 0. Cache
@Service
@RequiredArgsConstructor
public class SearchCacheReadService {

    private final SearchQueryRepository searchQueryRepository;

    // 1. Song
    @Cacheable(cacheNames = "search:songs", keyGenerator = "searchKeyGenerator")
    public Page<SongItem> songs(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        return searchQueryRepository.searchSongs(q, from, to, sort, pageable);
    }

    // 2. albums
    @Cacheable(cacheNames = "search:albums", keyGenerator = "searchKeyGenerator")
    public Page<AlbumItem> albums(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable) {
        return searchQueryRepository.searchAlbums(q, from, to, sort, pageable);
    }

    // 3. artists
    @Cacheable(cacheNames = "search:artists", keyGenerator = "searchKeyGenerator")
    public Page<ArtistItem> artists(String q, SearchSort sort, Pageable pageable) {
        return searchQueryRepository.searchArtists(q, sort, pageable);
    }
}