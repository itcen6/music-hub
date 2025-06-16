package com.weatherdecen.musichub.music.service;

import com.weatherdecen.musichub.music.dto.BroadcastMusic;
import com.weatherdecen.musichub.music.dto.RecommendMusic;

import java.util.List;

/**
 * Music Service Interface
 *
 * @since 2025-05-24
 * @author 전하은
 * */
public interface IMusicService {

    /**
     * 브로드캐스트 음악 송신
     * @since 2025-05-27
     */
    void handleBroadcastMessage();

    /**
     * 재생 중인 음악 정보 업데이트
     * @since 2025-05-27
     */
    void updateCurrentPlayingMusic();

    /**
     * 브로드캐스트 음악 정보 조회
     * @since 2025-05-27
     */
    BroadcastMusic getCurrentPlayingMusic();
}
