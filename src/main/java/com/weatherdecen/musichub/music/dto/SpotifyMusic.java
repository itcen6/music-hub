package com.weatherdecen.musichub.music.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class SpotifyMusic {

	private String musicTitle;
	private String musicTitleEn;
	private String musicArtist;
	private String musicArtistEn;
	private String musicAlbum;
	private String musicImage;
	private String musicReleaseDate;
	private long spotifyMusicDuration;

	public static SpotifyMusic of(Track track, String imageUrl) {
		return SpotifyMusic.builder()
			.musicTitle(track.getName())
			.musicArtist(track.getArtists().get(0).getName())
			.musicAlbum(track.getAlbum().getName())
			.musicImage(imageUrl)
			.spotifyMusicDuration(track.getDuration_ms())
			.build();
	}
}
