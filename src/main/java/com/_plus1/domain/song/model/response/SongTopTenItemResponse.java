package com._plus1.domain.song.model.response;

import com._plus1.domain.song.model.dto.SongDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SongTopTenItemResponse {

    private final Long songId;
    private final String title;
    private final List<String> artists;

    public static SongTopTenItemResponse from(SongDto dto, List<String> artists) {
        return new SongTopTenItemResponse(
                dto.getId(),
                dto.getTitle(),
                artists
        );
    }
}