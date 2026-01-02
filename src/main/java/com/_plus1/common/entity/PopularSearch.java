package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "popular_searches")
@Getter
@NoArgsConstructor
public class PopularSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private Long count;

    public PopularSearch (String keyword, Long count) {
        this.keyword = keyword;
        this.count = count;
    }
}
