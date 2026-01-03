package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song_artist",
        uniqueConstraints = @UniqueConstraint(name="uk_song_artist_id",
                columnNames = {"song_id","artist_id"}))
@Getter
@NoArgsConstructor
public class SongArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    public SongArtist(Song song, Artist artist) {
        this.song = song;
        this.artist = artist;
    }
}
