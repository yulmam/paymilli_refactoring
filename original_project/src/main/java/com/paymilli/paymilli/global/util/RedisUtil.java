package com.paymilli.paymilli.global.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveDataToRedis(String key, Object value, long expireTime) {
        redisTemplate.opsForValue().set(
            key,
            value,
            expireTime,
            TimeUnit.MILLISECONDS
        );
    }

    public Object getDataFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void removeDataFromRedis(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
