package com.weatherdecen.musichub.music.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.weatherdecen.musichub.music.dto.BroadcastMusic;
import com.weatherdecen.musichub.music.dto.RecommendMusic;
import com.weatherdecen.musichub.music.service.impl.MusicService;
import com.weatherdecen.musichub.music.service.impl.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;


/**
 * 테스트
 * @since 2025-05-24
 * @author 전하은
 * */
@ExtendWith(MockitoExtension.class)
public class MusicServiceTest {
    @InjectMocks
    private MusicService musicService;

    @Mock
    private RedisService redisService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void handleBroadcastMessage_shouldSendMessage_whenMusicIsNotNull() {
            // given
            RecommendMusic mockMusic =RecommendMusic.builder().title("Title").musicLength(180000).build(); // 3분
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            when(redisService.getCurrentMusic()).thenReturn(mockMusic);
            when(redisService.getPlayTime()).thenReturn(now);

            // when
            musicService.handleBroadcastMessage();

            // then
            ArgumentCaptor<BroadcastMusic> captor = ArgumentCaptor.forClass(BroadcastMusic.class);
            verify(messagingTemplate).convertAndSend(eq("/broadcast/music"), captor.capture());
            String title = captor.getValue().getMusic().getTitle();
            assertEquals("Title", captor.getValue().getMusic().getTitle());
        }

        @Test
        void handleBroadcastMessage_shouldReturn_whenMusicIsNull() {
            when(redisService.getCurrentMusic()).thenReturn(null);
            musicService.handleBroadcastMessage();
            verifyNoInteractions(messagingTemplate);
        }

        @Test
        void updateCurrentPlayingMusic_shouldResetPlaylist_whenIndexOutOfBounds() {
            List<RecommendMusic> playlist = List.of(RecommendMusic.builder().title("A").musicLength(100000).build());
            when(redisService.getPlayList()).thenReturn(playlist);
            when(redisService.getIndex()).thenReturn(5); // out of bounds

            musicService.updateCurrentPlayingMusic();

            verify(redisService).setIndex(0);
            verify(redisService).setPlayTime();
        }

        @Test
        void updateCurrentPlayingMusic_shouldDoNothing_whenIndexInBounds() {
            List<RecommendMusic> playlist = List.of(RecommendMusic.builder().title("A").musicLength(100000).build());
            when(redisService.getPlayList()).thenReturn(playlist);
            when(redisService.getIndex()).thenReturn(0);

            musicService.updateCurrentPlayingMusic();

            // 여기서는 setIndex, setPlayTime 호출이 없어야 함
            verify(redisService, never()).setIndex(0);
            verify(redisService, never()).setPlayTime();
        }
    }
