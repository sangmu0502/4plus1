package com._plus1.domain.search.model.dto.docs;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistDoc(
        Long artistId,
        Long externalId,
        String name
) {}