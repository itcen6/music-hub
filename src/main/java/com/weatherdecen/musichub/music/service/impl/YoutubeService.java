package com.weatherdecen.musichub.music.service.impl;

import com.google.api.services.youtube.model.ResourceId;
import com.weatherdecen.musichub.common.exception.CustomException;
import com.weatherdecen.musichub.common.exception.ErrorCode;
import com.weatherdecen.musichub.music.dto.SpotifyMusic;
import com.weatherdecen.musichub.music.dto.YoutubeMusic;
import com.weatherdecen.musichub.music.service.IYoutubeService;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.util.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Youtube Service
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class YoutubeService implements IYoutubeService {

	private final YouTube youtubeApi;
	private final RedisTemplate<String, Object> redisTemplate;

	public YoutubeMusic searchYoutubeMusic(SpotifyMusic spotifyMusic) {
		if (isNull(spotifyMusic)) return null;

		List<SearchResult> searchYoutubeResult = searchYoutubeResult(spotifyMusic);
		long musicLength = spotifyMusic.getSpotifyMusicDuration();

		return searchYoutubeResult.stream()
				.filter(result -> { return isValidData(result.getId()); })
				.map(result -> {
					Video video = getYoutubeVideo(result.getId()).getFirst();

					double score = calculateScore(video, musicLength);
					return new AbstractMap.SimpleEntry<>(YoutubeMusic.of(video.getId(), musicLength), score);
				})
				.sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 점수 높은 순
				.map(Map.Entry::getKey)
				.findFirst()
				.orElse(null);
    }

	private boolean isValidData(ResourceId videoId){
		if(isNull(videoId) || isNull(videoId.getVideoId())) return false;

		List<Video> videos = getYoutubeVideo(videoId);
		if (isEmpty(videos)) return false;

		return true;
	}

	private List<Video> getYoutubeVideo(ResourceId videoId) {
        try {
            return youtubeApi.videos()
                .list(Arrays.asList("id", "statistics", "contentDetails"))
                .setId(Collections.singletonList(videoId.getVideoId()))
                .execute()
                .getItems();
        } catch (IOException e) {
			log.error("Failed to get YouTube video for id: {}", videoId.getVideoId(), e);
			throw new CustomException(ErrorCode.YOUTUBE_NOT_FOUND, Map.of("videoId", videoId), msg->log.error("비디오 아이디 값을 찾는 중 에러 발생"), e);
        }
    }

	private List<SearchResult> searchYoutubeResult(SpotifyMusic spotifyMusic) {
		String query = spotifyMusic.getMusicTitle() + " " + spotifyMusic.getMusicArtist();

        try {
			YouTube.Search.List searchRequest = youtubeApi.search().list(Arrays.asList("id", "snippet"));
			searchRequest.setQ(query);
			searchRequest.setType(Arrays.asList("video"));
			searchRequest.setMaxResults(3L);
			searchRequest.setFields("items(id(videoId),snippet(publishedAt,channelId,title,description))");

			return searchRequest.execute().getItems();
        } catch (IOException e) {
			log.info("Fail to search youtube music : {}", e);
			throw new CustomException(ErrorCode.YOUTUBE_SEARCH_ERROR, Map.of("searchMusic", spotifyMusic), msg->log.error("유튜브 검색 중 에러 발생"), e);
        }
	}

	private static long getVideoLength(Video video) {
		return Duration.parse(video.getContentDetails().getDuration()).toMillis();
	}

	private double calculateScore(Video video, long musicLength) {
		long durationDiff = Math.abs(musicLength - getVideoLength(video));
		BigInteger viewCount = video.getStatistics().getViewCount();

		// 1. 조회수는 로그 스케일로 정규화 (예: 100,000 이상이면 가중치 충분히 높게)
		double viewScore = Math.log10(viewCount.doubleValue() + 1);

		// 2. 길이 차이는 작을수록 좋음 → 반대로 점수화
		double durationPenalty = durationDiff / 1000.0; // 초 단위 차이
		double durationScore = Math.max(0, 10 - durationPenalty); // 10초 안 넘으면 가점, 넘으면 0

		return (viewScore * 0.6) + (durationScore * 0.4);
	}
}
