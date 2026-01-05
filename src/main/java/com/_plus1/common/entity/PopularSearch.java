package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "popular_searches",
        uniqueConstraints = {@UniqueConstraint(name="uk_popular_keyword", columnNames = "keyword")})
@Getter
@NoArgsConstructor
public class PopularSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private Long count = 0L;

    public PopularSearch (String keyword, Long count) {
        this.keyword = keyword;
        this.count = count;
    }

    public void increment(){
        this.count++;
    }
}