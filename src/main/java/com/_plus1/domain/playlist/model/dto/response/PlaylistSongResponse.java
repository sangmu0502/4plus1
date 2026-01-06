package com._plus1.domain.playlist.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaylistSongResponse {

    private Long songId;
    private String title;
    private List<String> artists;


}
