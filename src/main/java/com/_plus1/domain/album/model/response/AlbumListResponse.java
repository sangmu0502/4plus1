package com._plus1.domain.album.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AlbumListResponse {

    private final List<AlbumItemDto> albums;

    public static AlbumListResponse from(List<AlbumItemDto> albums) {
        return new AlbumListResponse(albums);
    }

}
