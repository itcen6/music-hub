package com.weatherdecen.musichub.music.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendMusic {
    private String title;
    private String artist;
    private String imageUrl;
    private String youtubeId;
    private long musicLength; // in seconds
}
