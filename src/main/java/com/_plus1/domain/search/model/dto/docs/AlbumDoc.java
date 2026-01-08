package com._plus1.domain.search.model.dto.docs;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AlbumDoc(
        Long albumId,
        Long externalId,
        String title,
        List<String> artistNames,
        LocalDate releaseDate,
        String titleNorm,
        List<String> artistNamesNorm
) {}