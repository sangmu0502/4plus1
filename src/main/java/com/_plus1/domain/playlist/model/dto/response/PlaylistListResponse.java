package com._plus1.domain.playlist.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaylistListResponse {

    private List<PlaylistListItemResponse> playlists;

    public static PlaylistListResponse of(List<PlaylistListItemResponse> playlists) {
        return new PlaylistListResponse(playlists);
    }
}
