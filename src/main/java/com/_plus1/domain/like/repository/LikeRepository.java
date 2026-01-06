package com._plus1.domain.like.repository;

import com._plus1.common.entity.Like;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndSong(User user, Song song);

    Optional<Like> findByUserAndSong(User user, Song song);

    List<Like> findAllByUserId(Long userId);

    @Query("select l from Like l " +
            "join fetch l.song s " +
            "join fetch s.songArtists sa " +
            "join fetch sa.artist " +
            "where l.user.id = :userId")
    List<Like> findAllByUserIdWithSongAndArtist(@Param("userId") Long userId);
}
