package com._plus1.domain.song.model.dto;

import com._plus1.common.entity.Album;
import com._plus1.common.entity.Song;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {

    private Long id;
    private String title;

    private LocalDate releaseDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long playCount;
    // AlbumDto에서 가져올 수 있음. 수정 가능
    private Album album;

    public static SongDto from(Song song) {
        return new SongDto(
                song.getId(),
                song.getTitle(),
                song.getReleaseDate(),
                song.getCreatedAt(),
                song.getUpdatedAt(),
                song.getPlayCount(),
                song.getAlbum()
        );
    }
}
