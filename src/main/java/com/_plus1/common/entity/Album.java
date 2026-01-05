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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="album_id", nullable=false)
    private Long externalId;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = true)
    private LocalDate releaseDate;

    public Album(String title, LocalDate releaseDate) {
        this.title = title;
        this.releaseDate = releaseDate;
    }

    // 2. Album : Album album = new Album(externalId, title, releaseDate, artistReference);
    public Album(Long externalId, String title, LocalDate releaseDate){
        this.externalId = externalId;
        this.title = title;
        this.releaseDate = releaseDate;
    }
}
