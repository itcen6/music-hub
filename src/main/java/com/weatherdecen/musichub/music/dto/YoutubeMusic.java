package com.weatherdecen.musichub.music.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class YoutubeMusic {

	private String musicYoutubeId;
	private long musicLength;

	public static YoutubeMusic of(String musicYoutubeId, long musicLength) {
		return new YoutubeMusic(musicYoutubeId, musicLength);
	}
}
