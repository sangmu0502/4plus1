package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "genres")
@Getter
@NoArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String genreCode;

    @Column(nullable = false)
    private String genreName;

    public Genre (String genreCode, String genreName) {
        this.genreCode = genreCode;
        this.genreName = genreName;
    }
}
