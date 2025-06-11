package com.weatherdecen.musichub.music.dto;

import lombok.AccessLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Artist {

	private ExternalUrls external_urls;
	private String href;
	private String id;
	private String name;
	private String type;
	private String uri;
}
