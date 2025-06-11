package com.weatherdecen.musichub.music.service;

import com.weatherdecen.musichub.music.dto.MusicFromGpt;
import com.weatherdecen.musichub.music.dto.SpotifyMusic;

/**
 * Spotify Service Interface
 *
 * @since 2025-05-24
 * @author 전하은
 * */
public interface ISpotifyService {
    /**
     * Spotify 음악 검색
     * @since 2025-05-24
     */
    SpotifyMusic searchSpotifyMusic(MusicFromGpt musicFromGpt);
}
