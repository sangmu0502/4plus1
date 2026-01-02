package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "albums")
@Getter
@NoArgsConstructor
public class Album extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;

    // QueryDsl 사용시 Fetch 필요한지 생각하기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    public Album(String title, LocalDate releaseDate, Artist artist) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.artist = artist;
    }
}
