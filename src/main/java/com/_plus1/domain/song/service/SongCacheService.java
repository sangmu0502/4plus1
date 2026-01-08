package com._plus1.domain.song.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOP_CACHE_PREFIX = "top:";
    private static final String ALL_SCOPE = "all";
    private static final Duration TOP_TTL = Duration.ofDays(1);

    private String topTenKey() {
        return TOP_CACHE_PREFIX + ALL_SCOPE;
    }

    // 캐시를 조회 하는 것
    public Set<Long> getTopTenSongIds() {
        ZSetOperations<String, Object> zSetOps =
                redisTemplate.opsForZSet();

        Set<Object> values =
                zSetOps.reverseRange(topTenKey(), 0, 9);

        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.stream()
                .map(v -> Long.valueOf(v.toString()))
                .collect(Collectors.toSet());
    }

    // 캐시를 저장 하는 것
    public void saveTopTenSongs(Set<ZSetOperations.TypedTuple<Object>> values) {
        ZSetOperations<String, Object> zSetOps =
                redisTemplate.opsForZSet();

        redisTemplate.delete(topTenKey());
        zSetOps.add(topTenKey(), values);
        redisTemplate.expire(topTenKey(), TOP_TTL);
    }

    // 캐시를 삭제하는 것
    public void deleteTopTenCache() {
        redisTemplate.delete(topTenKey());
    }
}
