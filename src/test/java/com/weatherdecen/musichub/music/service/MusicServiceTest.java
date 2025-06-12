package com.weatherdecen.musichub.music.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.List;

import com.weatherdecen.musichub.music.dto.RecommendMusic;
import com.weatherdecen.musichub.music.service.impl.MusicService;
import com.weatherdecen.musichub.music.service.impl.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;


/**
 * 테스트
 * @since 2025-05-24
 * @author 전하은
 * */
@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AllArgsConstructor
public class MusicServiceTest {
    private final MusicService musicService;
    private final RedisService redisService;

    @Test
    public void initBroadcast_withNullPrompt_shouldThrowException() {
        Double prompt = null;

        assertThrows(IllegalArgumentException.class, () -> {
            musicService.initRecommendMusics(prompt);
        });
    }

    @Test
    public void initBroadcast_dataVerification() {
        Double prompt = 7.0;

        musicService.initRecommendMusics(prompt);

        List<RecommendMusic> recommendMusics = redisService.getPlayList();
        Integer index = redisService.getIndex();
        ZonedDateTime playTime = redisService.getPlayTime();

        assertThat(recommendMusics).isNotNull();
        assertThat(recommendMusics.size()).isNotEqualTo(0);
        assertEquals(0, index);
        assertThat(playTime).isNotNull();
    }
}
