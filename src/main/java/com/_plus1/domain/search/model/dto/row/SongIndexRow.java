package com._plus1.domain.search.model.dto.row;

import java.time.LocalDate;

public record SongIndexRow(
        Long id,
        Long externalId,
        String title,
        String albumTitle,
        LocalDate releaseDate,
        Long playCount
) {
}
