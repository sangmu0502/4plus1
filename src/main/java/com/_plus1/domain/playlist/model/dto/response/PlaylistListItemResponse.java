package com._plus1.domain.playlist.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlaylistListItemResponse {

    private Long playlistId;
    private String title;
    private long includedSong;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
