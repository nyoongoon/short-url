package com.shorturl.shorturl.infrastructor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyGenerator {

    private final StringRedisTemplate stringRedisTemplate;

    public Long generateKey(String keyPrefix) {
        return stringRedisTemplate.opsForValue().increment(keyPrefix);
    }
}
