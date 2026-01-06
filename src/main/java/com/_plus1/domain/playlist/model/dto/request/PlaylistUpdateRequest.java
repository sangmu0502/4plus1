package com._plus1.domain.playlist.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistUpdateRequest {

    @NotBlank(message = "플레이리스트 제목은 필수입니다.")
    private String title;

    private String description;
}
