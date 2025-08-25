package dev.ecznt.worker_demo.service;


import dev.ecznt.worker_demo.model.JobRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Generates a consistent cache key based on the job request parameters.
     * This ensures that the same query always hits the same cache key.
     */
    private String generateCacheKey(JobRequest request) {
        return String.format("query:%s:daterange:%s-%s", request.getQueryParam(), request.getDt1(), request.getDt2());
    }

    public Optional<Object> getQueryResult(JobRequest request) {
        String key = generateCacheKey(request);
        log.info("Checking cache for key: {}", key);
        Object result = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(result);
    }

    public void cacheQueryResult(JobRequest request, Object result) {
        String key = generateCacheKey(request);
        log.info("Storing result in cache for key: {}. TTL: 1 hour.", key);
        // Set a Time-To-Live (TTL) to automatically evict stale data.
        redisTemplate.opsForValue().set(key, result, Duration.ofHours(1));
    }
}
