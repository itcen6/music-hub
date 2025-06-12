package com.weatherdecen.musichub.music.service.impl;

import com.weatherdecen.musichub.music.dto.BroadcastMusic;
import com.weatherdecen.musichub.music.dto.RecommendMusic;
import com.weatherdecen.musichub.music.dto.MusicFromGpt;
import com.weatherdecen.musichub.music.dto.SpotifyMusic;
import com.weatherdecen.musichub.music.dto.YoutubeMusic;
import com.weatherdecen.musichub.music.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;



/**
 * Music Service
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class MusicService implements IMusicService {
    private final ISpotifyService spotifyService;
    private final IYoutubeService youtubeService;
    private final IGptService gptService;
    private final IRedisService redisService;

    private final SimpMessagingTemplate messagingTemplate;
    private final RedissonClient redissonClient;

    public Boolean initRecommendMusics(Double prompt) {
        Assert.notNull(prompt, "prompt must not be null");

        initPlaylist();
        setPlaylist(prompt.toString());

        return true;
    }

    public void handleBroadcastMessage() {
        BroadcastMusic music = getCurrentPlayingMusic();
        if(isNull(music)) return;

        sendMusicUpdateToClients(music);
        log.info("[브로드캐스트 음악 송신 - 음악 정보 : {}, 현재 재생 초 : {}]", music.getMusic().getTitle(), music.getStartTime());
    }

    public void updateCurrentPlayingMusic() {
        resetIndexIfOutOfBounds();
        setCurrentPlayingMusic();
    }

    public BroadcastMusic getCurrentPlayingMusic() {
        RecommendMusic music = redisService.getCurrentMusic();
        if(isNull(music)) return null;

        ZonedDateTime playTime = redisService.getPlayTime();
        if(isNull(playTime)) return null;

        long elapsed = Duration.between(playTime, ZonedDateTime.now(ZoneId.of("Asia/Seoul"))).getSeconds();
        return BroadcastMusic.builder().music(music).startTime(elapsed).build();
    }

    public List<RecommendMusic> getPlaylist() {
        return redisService.getPlayList();
    }

    private void initPlaylist() {
        redisService.setIndex(0);
        redisService.setPlayTime();
    }

    private void setPlaylist(String prompt) {
        redisService.deletePlayList();
        for(MusicFromGpt musicFromGpt : getRecommendMusicsFromGpt(prompt)) {
            RecommendMusic music = getValidateMusic(musicFromGpt);
            redisService.setMusic(music);
        }
    }

    private void resetIndexIfOutOfBounds() {
        if(!isEmpty(redisService.getPlayList()) && (redisService.getIndex() > redisService.getPlayList().size() - 1)){
            initPlaylist();
        }
    }

    private List<MusicFromGpt> getRecommendMusicsFromGpt(String prompt) {
        return gptService.getRecommendMusicsFromGpt(prompt);
    }

    private RecommendMusic getValidateMusic(MusicFromGpt music){
        SpotifyMusic spotifyMusic = spotifyService.searchSpotifyMusic(music);
        if(spotifyMusic == null) return null;

        YoutubeMusic youtubeMusic = youtubeService.searchYoutubeMusic(spotifyMusic);
        if(youtubeMusic == null) return null;

        return RecommendMusic.builder()
                .title(spotifyMusic.getMusicTitle())
                .artist(spotifyMusic.getMusicArtist())
                .imageUrl(spotifyMusic.getMusicImage())
                .youtubeId(youtubeMusic.getMusicYoutubeId())
                .musicLength(youtubeMusic.getMusicLength())
                .build();
    }

    private void sendMusicUpdateToClients(BroadcastMusic broadcastMusic) {
        messagingTemplate.convertAndSend("/api/music", broadcastMusic);
    }

    private void setCurrentPlayingMusic() {
        RecommendMusic music = redisService.getCurrentMusic();
        if(isNull(music)) return;

        Integer index = redisService.getIndex();
        if(isNull(index)) return;

        ZonedDateTime playTime = redisService.getPlayTime();
        if(isNull(playTime)) return;

        String threadName = Thread.currentThread().getName();

        if (isMusicStart(index, playTime)){
            handleWithLock(() -> {
                redisService.publishToRedisChannel(index + 1);
                log.info("🔒 곡 시작 : 락 획득 by thread [{}]", threadName);
            });
            return;
        }

        if (isMusicFinish(music, playTime)) {
            handleWithLock(() -> {
                redisService.setIndex(index + 1);
                redisService.setPlayTime();

                redisService.publishToRedisChannel(index + 1);
                log.info("🔒 곡 종료 : 락 획득 by thread [{}]", threadName);
            });
        }
    }

    private static boolean isMusicStart(int index, ZonedDateTime startTime) {
        ZonedDateTime now = ZonedDateTime.now();
        return index == 0 && now.toEpochSecond() == startTime.toEpochSecond();
    }

    private static boolean isMusicFinish(RecommendMusic currentMusic, ZonedDateTime startTime) {
        long elapsedSeconds = Duration.between(startTime, ZonedDateTime.now(ZoneId.of("Asia/Seoul"))).getSeconds();
        return elapsedSeconds >= (currentMusic.getMusicLength() / 1000);
    }

    private void handleWithLock(Runnable task) {
        RLock lock = redissonClient.getLock("music-play-lock");

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (isLocked) {
                task.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
