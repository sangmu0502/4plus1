package com._plus1.domain.search.model.dto.response;

import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import org.springframework.data.domain.Slice;

public record SearchSliceResponse(
        String q,
        Slice<SongItem> songs,
        Slice<AlbumItem> albums,
        Slice<ArtistItem> artists
) {
}
