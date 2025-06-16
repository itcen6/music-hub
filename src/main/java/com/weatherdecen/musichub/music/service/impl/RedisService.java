package com.weatherdecen.musichub.music.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherdecen.musichub.common.exception.CustomException;
import com.weatherdecen.musichub.common.exception.ErrorCode;
import com.weatherdecen.musichub.common.redis.RedisPublisher;
import com.weatherdecen.musichub.music.dto.RecommendMusic;
import com.weatherdecen.musichub.music.service.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Redis Service
 *
 * @since 2025-05-28
 * @author 전하은
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService implements IRedisService {
    private final String PLAYLIST_KEY = "music:playlist";
    private final String INDEX_KEY = "music:index";
    private final String PLAY_TIME_KEY = "music:playTime";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final RedisPublisher publisher;
    private final ObjectMapper objectMapper;

    public Integer getIndex() {
        String index = (String) redisStringTemplate.opsForValue().get(INDEX_KEY);
        return index == null ? null : Integer.parseInt(index);
    }

    public ZonedDateTime getPlayTime(){
        String startTime = (String) redisStringTemplate.opsForValue().get(PLAY_TIME_KEY);
        return ZonedDateTime.parse(startTime);
    }

    public List<RecommendMusic> getPlayList() {
        List<String> playlist = redisStringTemplate.opsForList().range(PLAYLIST_KEY, 0, -1);
        if(isEmpty(playlist)) return null;

        return playlist.stream().map(json -> {
            try {
                return objectMapper.readValue(json, RecommendMusic.class);
            } catch (JsonProcessingException e) {
                throw new CustomException(ErrorCode.JSON_PASING_ERROR, Map.of("json", json), msg->log.error("redis 값을 json으로 파싱중 에러 발생"), e);
            }
        }).collect(Collectors.toList());
    }

    public RecommendMusic getCurrentMusic() {
        List<RecommendMusic> recommendMusics = getPlayList();
        if(isNull(recommendMusics)) return null;

        Integer index = getIndex();
        if(isNull(index)) return null;

        if(noIndexContent(recommendMusics, index)) return null;
        return recommendMusics.get(getIndex());
    }

    public void setMusic(RecommendMusic music){
        redisTemplate.opsForList().rightPush(PLAYLIST_KEY, music);
    }

    public void setIndex(int index){
        redisTemplate.opsForValue().set(INDEX_KEY, index);
    }

    public void setPlayTime(){ redisStringTemplate.opsForValue().set(PLAY_TIME_KEY, Instant.now().atZone(ZoneId.of("Asia/Seoul")).toString()); }

    public void publishToRedisChannel(Integer index) { publisher.publish("broadcast-channel", index); }

    private static boolean noIndexContent(List<RecommendMusic> recommendMusics, Integer index) {
        return recommendMusics.size() <= index;
    }
}
