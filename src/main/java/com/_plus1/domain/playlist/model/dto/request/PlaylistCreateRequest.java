package com._plus1.domain.playlist.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaylistCreateRequest {

    @NotBlank(message = "플레이리스트 제목은 필수입니다.")
    private String title;

    private String description;

}