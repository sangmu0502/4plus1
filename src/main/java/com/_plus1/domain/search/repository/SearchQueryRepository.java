package com._plus1.domain.search.repository;


import com._plus1.domain.search.model.dto.cache.SearchKey;
import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public interface SearchQueryRepository {

    Page<SongItem> searchSongs(SearchKey condition);
    Page<AlbumItem> searchAlbums(SearchKey condition);
    Page<ArtistItem> searchArtists(SearchKey condition);

    Slice<SongItem> searchSongsSlice(SearchKey condition);
    Slice<AlbumItem> searchAlbumsSlice(SearchKey condition);
    Slice<ArtistItem> searchArtistsSlice(SearchKey condition);
}
