package com._plus1.domain.song.model.response;

import com._plus1.domain.song.model.dto.SongDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongPlayResponse {

    private final Long songId;
    private final long playCount;

    public static SongPlayResponse from(SongDto dto) {
        return new SongPlayResponse(
                dto.getId(),
                dto.getPlayCount()
        );
    }
}
