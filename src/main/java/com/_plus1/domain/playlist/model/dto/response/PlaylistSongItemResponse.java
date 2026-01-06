package com._plus1.domain.playlist.model.dto.response;

import com._plus1.common.entity.PlaylistSong;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class PlaylistSongItemResponse {

    private Long playlistSongId;
    private Integer sortOrder;
    private Long songId;
    private String title;
    private List<String> artists;

    public static PlaylistSongItemResponse from(PlaylistSong ps, Map<Long, List<String>> artistsBySongId) {

        return new PlaylistSongItemResponse(
                ps.getId(),
                ps.getSortOrder(),
                ps.getSong().getId(),
                ps.getSong().getTitle(),
                artistsBySongId.getOrDefault(ps.getSong().getId(), List.of())

        );


    }
}
