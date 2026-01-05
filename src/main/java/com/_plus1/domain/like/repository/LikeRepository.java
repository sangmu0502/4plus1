package com._plus1.domain.like.repository;

import com._plus1.common.entity.Like;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndSong(User user, Song song);

}
