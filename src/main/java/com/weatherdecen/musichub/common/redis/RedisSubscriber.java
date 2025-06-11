package com.weatherdecen.musichub.common.redis;

import com.weatherdecen.musichub.music.service.IMusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/**
 * Redis Subscriber
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final IMusicService musicService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        musicService.handleBroadcastMessage();
    }
}
