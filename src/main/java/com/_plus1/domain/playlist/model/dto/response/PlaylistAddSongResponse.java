package com._plus1.domain.playlist.model.dto.response;

import com._plus1.common.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PlaylistAddSongResponse {

    private Long playlistId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PlaylistSongResponse> playlistSongs;

    public static PlaylistAddSongResponse from (Playlist playlist, List<PlaylistSongResponse> playlistSongs) {
        return new PlaylistAddSongResponse(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getDescription(),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt(),
                playlistSongs
        );
    }

}
