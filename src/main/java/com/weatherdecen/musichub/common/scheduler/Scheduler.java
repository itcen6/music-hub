package com.weatherdecen.musichub.common.scheduler;

import com.weatherdecen.musichub.music.service.IMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private final IMusicService musicService;

    @Scheduled(fixedRate = 1000)
    public void updateCurrentPlayingMusic() {
        musicService.updateCurrentPlayingMusic();
    }
}
