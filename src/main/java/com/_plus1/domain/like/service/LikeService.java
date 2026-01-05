package com._plus1.domain.like.service;

import com._plus1.common.entity.Like;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.User;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.like.model.dto.LikeDto;
import com._plus1.domain.like.model.response.LikeResponse;
import com._plus1.domain.like.repository.LikeRepository;
import com._plus1.domain.song.repository.SongRepository;
import com._plus1.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final SongRepository songRepository;
    private final UserService userService;

    // 좋아요 생성
    @Transactional
    public LikeResponse createLike(Long songId) {

        // 로그인 유저 조회
        User user = userService.getCurrentUser();

        // 노래 조회
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(ErrorCode.SONG_NOT_FOUND));

        // 중복 좋아요 체크
        if (likeRepository.existsByUserAndSong(user, song)) {
            throw new CustomException(ErrorCode.LIKE_ALREADY_EXISTS);
        }

        // Like 생성 & 저장
        Like like = new Like(user, song);
        Like savedLike = likeRepository.save(like);

        // DTO -> Response 변환
        return LikeResponse.from(
                LikeDto.from(savedLike)
        );
    }

    // 좋아요 삭제
    @Transactional
    public void deleteLike(Long songId) {

        // 로그인 유저 조회
        User user = userService.getCurrentUser();

        // 노래 조회
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(ErrorCode.SONG_NOT_FOUND));

        // 좋아요 조회
        Like like = likeRepository.findByUserAndSong(user, song)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        // 삭제
        likeRepository.delete(like);
    }
}
