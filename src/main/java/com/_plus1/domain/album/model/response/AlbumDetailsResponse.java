package com._plus1.domain.album.model.response;

import com._plus1.common.entity.Album;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate; // album.getReleaseDate()
import java.util.List;

@Getter
@AllArgsConstructor
public class AlbumDetailsResponse {

    private final Long albumId;
    private final String title;
    private final LocalDate releaseDate;
    private final String albumArtistName;
    private final List<AlbumGenreDto> genres;
    private final List<AlbumSongDto> songs;

    public static AlbumDetailsResponse from(
            Album album,
            String albumArtistName,
            List<AlbumGenreDto> genres,
            List<AlbumSongDto> songs
    ) {
        return new AlbumDetailsResponse(
                album.getId(),
                album.getTitle(),
                album.getReleaseDate(),
                albumArtistName,
                genres,
                songs
        );
    }

}
