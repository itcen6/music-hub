package com.weatherdecen.musichub.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis Publisher
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
