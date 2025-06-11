package com.weatherdecen.musichub.music.service;

import com.weatherdecen.musichub.music.dto.RecommendMusic;

import java.util.List;

/**
 * Spotify Service Interface
 *
 * @since 2025-05-28
 * @author 전하은
 * */
public interface IRedisService {
    /**
     * 레디스 음악 인덱스 조회
     * @since 2025-05-26
     */
    Integer getIndex();

    /**
     * 레디스 음악 재생시간 조회
     * @since 2025-05-26
     */
    Long getStartTime();

    /**
     * 레디스 플레이리스트 조회
     * @since 2025-05-26
     */
    List<RecommendMusic> getPlayList();

    /**
     * 레디스 재생 중인 음악 조회
     * @since 2025-05-26
     */
    RecommendMusic getCurrentMusic() ;

    /**
     * 레디스 플레이리스트 저장
     * @since 2025-05-26
     */
    void setMusic(RecommendMusic music);

    /**
     * 레디스 음악 인덱스 저장
     * @since 2025-05-26
     */
    void setIndex(int index);

    /**
     * 레디스 음악 재생시간 저장
     * @since 2025-05-26
     */
    void setStartTime(Long startTime);

    /**
     * 레디스 기존 playlist 삭제
     * @since 2025-06-04
     */
    void deletePlayList();

    /**
     * 레디스 채널 생성
     * @since 2025-05-26
     */
    void publishToRedisChannel(Integer index);
}
