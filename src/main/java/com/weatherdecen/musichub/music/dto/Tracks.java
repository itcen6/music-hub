package com.weatherdecen.musichub.music.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Tracks {

	private String href;
	private int limit;
	private String next;
	private int offset;
	private String previous;
	private int total;
	private List<Track> items;
}
