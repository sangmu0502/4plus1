package com._plus1.domain.search.repository;


import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


public interface SearchQueryRepository {

    Page<SongItem> searchSongs(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable);
    Page<AlbumItem> searchAlbums(String q, LocalDate from, LocalDate to, SearchSort sort, Pageable pageable);
    Page<ArtistItem> searchArtists(String q, SearchSort sort, Pageable pageable);
}
