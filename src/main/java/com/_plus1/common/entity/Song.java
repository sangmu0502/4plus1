package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "songs")
@Getter
@NoArgsConstructor
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="song_id",nullable = false)
    private Long externalId;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column
    private LocalDate releaseDate;

    @Column(nullable = false)
    private long playCount=0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    public Song (String title, LocalDate releaseDate, Long playCount, Album album) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.playCount = playCount;
        this.album = album;
    }

    // 4. Song : Song song = new Song(externalId, title, releaseDate, albumReference);
    public Song(Long externalId, String title, LocalDate releaseDate, Album albumReference){
        this.externalId = externalId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.album = albumReference;
    }

    // PlayCount 증가 메서드 추가
    public void increasePlayCount() {
        this.playCount++;
    }
}