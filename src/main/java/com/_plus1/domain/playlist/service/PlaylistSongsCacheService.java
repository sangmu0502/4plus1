package com._plus1.domain.playlist.service;
import com._plus1.domain.playlist.model.dto.response.PlaylistSongsPageCache;
import com._plus1.domain.playlist.model.dto.response.PlaylistSongItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaylistSongsCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "playlist:songs:firstpage:";

    public String buildFirstPageKey(Long userId, Long playlistId, int size) {

        return CACHE_PREFIX + "uid:" + userId + ":pid:" + playlistId + ":size:" + size;
    }

    public Page<PlaylistSongItemResponse> getFirstPage(Long userId, Long playlistId, int size) {
        String key = buildFirstPageKey(userId, playlistId, size);
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached == null) {
            return null;
        }

        PlaylistSongsPageCache dto = (PlaylistSongsPageCache) cached;

        return new PageImpl<>(dto.getContent(), PageRequest.of(dto.getPageNumber(), dto.getPageSize()), dto.getTotalElements());

    }

    public void saveFirstPage(Long userId, Long playlistId, int size, Page<PlaylistSongItemResponse> page) {
        String key = buildFirstPageKey(userId, playlistId, size);

        PlaylistSongsPageCache dto = new PlaylistSongsPageCache(new java.util.ArrayList<>(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements());
        redisTemplate.opsForValue().set(key, dto, 24, TimeUnit.HOURS);

    }

    public void deleteFirstPage(Long userId, Long playlistId, int size) {
        String key = buildFirstPageKey(userId, playlistId, size);
        redisTemplate.delete(key);

    }

}


