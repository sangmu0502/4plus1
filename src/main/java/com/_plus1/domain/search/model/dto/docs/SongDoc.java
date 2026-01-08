package com._plus1.domain.search.model.dto.docs;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SongDoc(
        Long songId,
        Long externalId,
        String title,
        String albumTitle,
        List<String> artistNames,
        LocalDate releaseDate,
        Long playCount
) {}