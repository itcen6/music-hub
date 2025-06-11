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
     * 추천 음악 조회 및 재생 정보 초기화
     * @since 2025-05-24
     */
    Boolean initRecommendMusics(Double prompt);

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

    /**
     * 플레이리스트 조회
     * @since 2025-06-02
     */
    List<RecommendMusic> getPlaylist();
}
