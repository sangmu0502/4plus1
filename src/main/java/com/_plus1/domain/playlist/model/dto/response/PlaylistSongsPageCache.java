package com._plus1.domain.playlist.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongsPageCache implements Serializable {
    private List<PlaylistSongItemResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;


}
