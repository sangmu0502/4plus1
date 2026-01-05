package com._plus1.domain.album.model.response;

import com._plus1.common.entity.Album;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumItemDto {

    private final Long id;
    private final String title;
    private final String artists;

    public static AlbumItemDto from(Album album, String artists) {
        return new AlbumItemDto(
                album.getId(),
                album.getTitle(),
                artists
        );
    }

}
