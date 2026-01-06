package com._plus1.domain.song.service;

import com._plus1.common.entity.Song;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.song.model.dto.SongDto;
import com._plus1.domain.song.model.response.SongPlayResponse;
import com._plus1.domain.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    // 노래 재생 api

    @Transactional
    public SongPlayResponse playSongs(Long songId) {

        // 노래 조회
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(ErrorCode.SONG_NOT_FOUND));

        // 재생 수 증가
        song.increasePlayCount();

        return SongPlayResponse.from(SongDto.from(song));
    }
}
