package com._plus1.domain.search.service.port;

import com._plus1.domain.search.model.dto.cache.SearchKey;

import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import org.springframework.data.domain.Slice;

public interface SearchEsPort {
    Slice<SongItem> searchSongsSlice(SearchKey key);
    Slice<AlbumItem> searchAlbumsSlice(SearchKey key);
    Slice<ArtistItem> searchArtistsSlice(SearchKey key);
}
