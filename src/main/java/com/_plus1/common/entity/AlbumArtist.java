package com._plus1.common.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name="album_artist",
        uniqueConstraints = @UniqueConstraint(name="uk_album_artist_id",
                columnNames = {"album_id","artist_id"}))
@Getter
public class AlbumArtist extends BaseEntity{

    protected AlbumArtist(){}

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="artist_id",nullable=false)
    private Artist artist;

    public AlbumArtist(Album album, Artist artist) {
        this.album = album;
        this.artist = artist;
    }
}
