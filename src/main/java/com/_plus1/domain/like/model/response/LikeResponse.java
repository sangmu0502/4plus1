package com._plus1.domain.like.model.response;

import com._plus1.domain.like.model.dto.LikeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponse {

    private final Long likeId;
    private final Long songId;

    public static LikeResponse from(LikeDto dto) {
        return new LikeResponse(
                dto.getId(),
                dto.getSong().getId()
        );
    }
}
