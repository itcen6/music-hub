package com.weatherdecen.musichub.music.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BroadcastMusic {
    private RecommendMusic music;
    private long startTime;
    private ZonedDateTime startDateTime;
}
