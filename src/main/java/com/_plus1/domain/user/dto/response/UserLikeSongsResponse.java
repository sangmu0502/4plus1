package com._plus1.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserLikeSongsResponse {

    private List<LikeSongResponse> likeSongs;

    // 내부 클래스
    @Getter
    @AllArgsConstructor
    public static class LikeSongResponse {
        private Long songId;
        private Long likeId;
        private String title;
        private List<String> artists;
    }
}
