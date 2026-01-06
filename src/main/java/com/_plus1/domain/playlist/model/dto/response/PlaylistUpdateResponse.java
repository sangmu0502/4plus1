package com._plus1.domain.playlist.model.dto.response;

import com._plus1.common.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlaylistUpdateResponse {

    private Long playlistId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlaylistUpdateResponse from(Playlist playlist) {
        return new PlaylistUpdateResponse(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getDescription(),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt()
        );

    }

}
