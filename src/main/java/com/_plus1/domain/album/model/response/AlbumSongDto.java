package com._plus1.domain.album.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AlbumSongDto {

    private final Long songId;
    private final String title;
    private final List<String> artists;

    public static AlbumSongDto from(Long songId, String title, List<String> artists) {
        return new AlbumSongDto(songId, title, artists);
    }

}
