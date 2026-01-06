package com._plus1.domain.song.model.response;

import com._plus1.common.entity.Song;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class SongOfPopularResponse {

    private Long id;
    private String title;
    private List<String> artists;
    private LocalDate releaseDate;

    public static SongOfPopularResponse from (
            Song song,
            List<String> artists
    ) {
        return new SongOfPopularResponse(
                song.getId(),
                song.getTitle(),
                artists,
                song.getReleaseDate()
        );
    }
}
