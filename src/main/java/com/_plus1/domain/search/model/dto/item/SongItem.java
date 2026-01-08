package com._plus1.domain.search.model.dto.item;


import java.time.LocalDate;

public record SongItem(
        Long id,
        Long externalId,
        String title,
        Long playCount,
        LocalDate releaseDate
)  {}