package com._plus1.domain.playlist.model.dto.response;

import com._plus1.common.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlaylistDetailResponse {

    private Long playlistId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long songCount;

    public static PlaylistDetailResponse from(Playlist playlist, long songCount) {
        return new PlaylistDetailResponse(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getDescription(),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt(),
                songCount
        );
    }

    
}
