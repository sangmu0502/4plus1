package com._plus1.domain.song.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SongTopTenResponse {

    private final List<SongTopTenItemResponse> results;
}
