package com.weatherdecen.musichub.music.controller;

import com.weatherdecen.musichub.common.dto.ApiResponse;
import com.weatherdecen.musichub.music.dto.BroadcastMusic;
import com.weatherdecen.musichub.music.service.IMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Music Controller
 *
 * @since 2025-05-22
 * @author 전하은
 * */
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final IMusicService musicService;

    @GetMapping("/initRecommendMusics")
    public ApiResponse<Boolean> initRecommendMusics(@RequestParam(name = "prompt") Double prompt) {
        return ApiResponse.createSuccess(musicService.initRecommendMusics(prompt));
    }

    @GetMapping("/getCurrentPlayingMusic")
    public ApiResponse<BroadcastMusic> getCurrentPlayingMusic() {
        return ApiResponse.createSuccess(musicService.getCurrentPlayingMusic());
    }
}
