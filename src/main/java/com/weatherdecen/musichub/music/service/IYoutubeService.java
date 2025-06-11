package com.weatherdecen.musichub.music.service;

import com.weatherdecen.musichub.music.dto.SpotifyMusic;
import com.weatherdecen.musichub.music.dto.YoutubeMusic;

/**
 * Youtube Service Interface
 *
 * @since 2025-05-24
 * @author 전하은
 * */
public interface IYoutubeService {
    /**
     * Youtube 음악 검색
     * @since 2025-05-26
     */
    YoutubeMusic searchYoutubeMusic(SpotifyMusic spotifyMusic);
}
