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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Long playCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    public Song (String title, LocalDate releaseDate, Long playCount, Album album) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.playCount = playCount;
        this.album = album;
    }
}
