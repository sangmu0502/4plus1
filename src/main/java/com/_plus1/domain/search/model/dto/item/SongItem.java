package com._plus1.domain.search.model.dto.item;

public record SongItem(
        Long id,
        Long externalId,
        String title,
        Long playCount
){}