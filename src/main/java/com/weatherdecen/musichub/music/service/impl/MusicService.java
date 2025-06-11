package com.weatherdecen.musichub.music.service.impl;

import com.weatherdecen.musichub.music.dto.BroadcastMusic;
import com.weatherdecen.musichub.music.dto.RecommendMusic;
import com.weatherdecen.musichub.music.dto.MusicFromGpt;
import com.weatherdecen.musichub.music.dto.SpotifyMusic;
import com.weatherdecen.musichub.music.dto.YoutubeMusic;
import com.weatherdecen.musichub.music.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
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

    public Boolean initRecommendMusics(Double prompt) {
        Assert.notNull(prompt, "prompt must not be null");

        initPlaylist();
        setPlaylist(prompt.toString());

        // 현재 노래 전송
        redisService.publishToRedisChannel(redisService.getIndex());

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

        Long startTime = redisService.getStartTime();
        if(isNull(startTime)) return null;

        return BroadcastMusic.builder().music(music).startTime(startTime).build();
    }

    public List<RecommendMusic> getPlaylist() {
        return redisService.getPlayList();
    }

    private void initPlaylist() {
        redisService.setIndex(0);
        redisService.setStartTime(0L);
    }

    private void setPlaylist(String prompt) {
        redisService.deletePlayList();
        for(MusicFromGpt musicFromGpt : getRecommendMusicsFromGpt(prompt)) {
            RecommendMusic music = getValidateMusic(musicFromGpt);
            redisService.setMusic(music);
        }
    }

    private void resetIndexIfOutOfBounds() {
        if(!isEmpty(redisService.getPlayList()) && (redisService.getIndex() > redisService.getPlayList().size())){
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

        Long startTime = redisService.getStartTime();
        if(isNull(startTime)) return;

        redisService.setStartTime(startTime + 1);

        if (isMusicStart(index, startTime)){
            redisService.publishToRedisChannel(index + 1);
        }

        if (isMusicFinish(music, startTime)) {
            redisService.setStartTime(0L);
            redisService.setIndex(index + 1);

            redisService.publishToRedisChannel(index + 1);
        }
    }

    private static boolean isMusicStart(int index, Long startTime) {
        return index == 0 && startTime == 0;
    }

    private static boolean isMusicFinish(RecommendMusic currentMusic, Long startTime) {
        return (startTime * 1000) >= currentMusic.getMusicLength();
    }
}
