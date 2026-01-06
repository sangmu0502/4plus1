package com._plus1.domain.search.model.dto.cache;

import java.time.LocalDate;

public record SearchKey(
        String q,
        LocalDate from,
        LocalDate to,
        String sort,
        int page,
        int size
) {
}
