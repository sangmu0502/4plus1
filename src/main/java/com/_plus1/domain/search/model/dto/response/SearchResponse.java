package com._plus1.domain.search.model.dto.response;

import com._plus1.domain.search.model.dto.item.AlbumItem;
import com._plus1.domain.search.model.dto.item.ArtistItem;
import com._plus1.domain.search.model.dto.item.SongItem;
import org.springframework.data.domain.Page;

import java.io.Serializable;


public record SearchResponse(
        String q,
        Page<SongItem> songs,
        Page<AlbumItem> albums,
        Page<ArtistItem> artists
){}
