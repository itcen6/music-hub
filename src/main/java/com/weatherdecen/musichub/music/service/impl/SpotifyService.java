package com.weatherdecen.musichub.music.service.impl;

import com.weatherdecen.musichub.music.dto.*;
import com.weatherdecen.musichub.music.service.ISpotifyService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Spotify Service
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService implements ISpotifyService {

	@Value("${spotify.client.id}")
	private String clientId;

	@Value("${spotify.client.secret}")
	private String clientSecret;

	@Qualifier("restTemplate")
	private final RestTemplate restTemplate;

	public SpotifyMusic searchSpotifyMusic(MusicFromGpt musicFromGpt) {
		if(isNull(musicFromGpt)) return null;

		SpotifyMusic musicKr = extractFirstMusic(getKoreaResponseEntity(musicFromGpt));
		SpotifyMusic musicEn = extractFirstMusic(getEnglishresponseEntity(musicFromGpt));

		return getSpotifyMusic(musicKr, musicEn);
	}

	private ResponseEntity<SearchResponse> getKoreaResponseEntity(MusicFromGpt music) {
		String query = getSearchQuery(music);
		HttpEntity<String> requestEntity = getSpotifyRequestEntity();

		return restTemplate.exchange(
				"https://api.spotify.com/v1/search?q={query}&type=track&limit=1&market=KR&locale=ko-KR",
				HttpMethod.GET,
				requestEntity,
				SearchResponse.class,
				query);
	}

	private ResponseEntity<SearchResponse> getEnglishresponseEntity(MusicFromGpt music) {
		String query = getSearchQuery(music);
		HttpEntity<String> requestEntity = getSpotifyRequestEntity();

		return restTemplate.exchange(
				"https://api.spotify.com/v1/search?q={query}&type=track&limit=1&market=KR&",
				HttpMethod.GET,
				requestEntity,
				SearchResponse.class,
				query);
	}

	private static String getSearchQuery(MusicFromGpt music) {
		String query = music.getTrack() + " " + music.getArtist();

		log.info("spotify query : {}", query);
		return query;
	}

	private HttpEntity<String> getSpotifyRequestEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(getAccessToken());

		return new HttpEntity<>(headers);
	}

	private String getAccessToken() {
		HttpEntity<MultiValueMap<String, String>> requestEntity = getAccessTokenRequestEntity();

		ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(
				"https://accounts.spotify.com/api/token",
				HttpMethod.POST,
				requestEntity,
				AccessTokenResponse.class
		);

		if(isNull(responseEntity.getBody())) return null;
		return responseEntity.getBody().getAccess_token();
	}

	private HttpEntity<MultiValueMap<String, String>> getAccessTokenRequestEntity() {
		HttpHeaders headers = getAccessTokenHeader();
		MultiValueMap<String, String> body = getAccessTokenBody();

		return new HttpEntity<>(body, headers);
	}

	private HttpHeaders getAccessTokenHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(clientId, clientSecret);

		return headers;
	}

	private static MultiValueMap<String, String> getAccessTokenBody() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "client_credentials");

		return body;
	}

	private static SpotifyMusic extractFirstMusic(ResponseEntity<SearchResponse> response) {
		Track track = getTrack(response);
		if (track == null) return null;

		String albumUrl = getAlbumUrl(track);

		return SpotifyMusic.of(track, albumUrl);
	}

	private static Track getTrack(ResponseEntity<SearchResponse> response) {
		if(isNull(response) || isNull(response.getBody()) || isNull(response.getBody().getTracks())) return null;

		Tracks tracks = response.getBody().getTracks();
		if(isNull(tracks) || isEmpty(tracks.getItems())) return null;

		return tracks.getItems().getFirst();
	}

	private static String getAlbumUrl(Track track) {
		Album album = track.getAlbum();
		if(isNull(album) || isEmpty(album.getImages())) return null;

		Image image = album.getImages().getFirst();
		if(isNull(image)) return null;

		return image.getUrl();
	}

	private SpotifyMusic getSpotifyMusic(SpotifyMusic musicKr, SpotifyMusic musicEn) {
		if(isNull(musicKr) && isNull(musicEn)) return null;
		if(isNull(musicKr)) return musicEn;
		return musicKr;
	}
}
