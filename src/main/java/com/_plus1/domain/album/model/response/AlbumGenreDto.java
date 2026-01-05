package com._plus1.domain.album.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumGenreDto {

    private final String genreCode;
    private final String genreName;

    public static AlbumGenreDto from(String genreCode, String genreName) {
        return new AlbumGenreDto(genreCode, genreName);
    }

}
