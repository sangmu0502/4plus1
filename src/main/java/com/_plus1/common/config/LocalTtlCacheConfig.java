package com._plus1.common.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@EnableCaching
@ConfigurationPropertiesScan
@Profile({"local-ttl", "test"})
public class LocalTtlCacheConfig {
    @Bean
    public CacheManager cacheManager(@Value("${app.cache.maximum-size:50000}") long maximumSize,
                                     @Value("${app.cache.expire-after-write-ms:300000}") long expireMs) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "searchCached:paged", "searchCached:sliced", "popularKeywords"
        );
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMillis(expireMs)) // 5분
                        .maximumSize(maximumSize)
                        .recordStats()
        );
        return cacheManager;
    }
}
