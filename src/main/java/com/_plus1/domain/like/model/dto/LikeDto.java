package com._plus1.domain.like.model.dto;

import com._plus1.common.entity.Like;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    private Long id;
    private User user;
    private Song song;

    public static LikeDto from(Like like) {
        return new LikeDto(
                like.getId(),
                like.getUser(),
                like.getSong()
        );
    }
}
