package com.weatherdecen.musichub.music.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Album {

	private String album_type;
	private List<Artist> artists;
	private String href;
	private String id;
	private List<Image> images;
	private String name;
	private String release_date;
	private String release_date_precision;
	private String type;
	private String uri;
	private boolean is_playable;
	private int total_tracks;
	private String album_group;
}
