package com.weatherdecen.musichub.music.service;

import com.weatherdecen.musichub.music.dto.MusicFromGpt;

import java.util.List;

/**
 * Gpt Service Interface
 *
 * @since 2025-05-27
 * @author 전하은
 * */
public interface IGptService {

    /**
     * Gpt 음악 추천 리스트 조회
     * @since 2025-05-24
     */
    List<MusicFromGpt> getRecommendMusicsFromGpt(String prompt);
}
