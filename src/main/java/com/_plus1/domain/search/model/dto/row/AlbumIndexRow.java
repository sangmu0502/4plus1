package com._plus1.domain.search.model.dto.row;

import java.time.LocalDate;

public record AlbumIndexRow(
        Long id,
        Long externalId,
        String title,
        LocalDate releaseDate
) {
}
