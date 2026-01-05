package com._plus1.common.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artists")
@Getter
@NoArgsConstructor
public class Artist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="artist_id", nullable=false)
    private Long externalId;

    @Column(nullable = false, length = 1000)
    private String name;

    public Artist(String name) {
        this.name = name;
    }

    // 1. Artist : Artist artist = new Artist(externalId, name);
    public Artist(Long externalId, String name){
        this.externalId = externalId;
        this.name = name;
    }
}
