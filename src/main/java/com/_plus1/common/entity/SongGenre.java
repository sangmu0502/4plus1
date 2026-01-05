package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song_genre",
        uniqueConstraints = {@UniqueConstraint(name="uk_song_genre_id",
                columnNames = {"song_id","genre_id"})})
@Getter
@NoArgsConstructor
public class SongGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public SongGenre(Song song, Genre genre) {
        this.song = song;
        this.genre = genre;
    }
}
