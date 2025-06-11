package com.weatherdecen.musichub.common.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;

/**
 * Youtube config
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Configuration
public class YoutubeConfig {

	@Value("${youtube.client.api-key}")
	private String apiKey;

	@Bean
	public YouTube youtubeApi() throws GeneralSecurityException, IOException {
		YouTube.Builder builder = new YouTube.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			GsonFactory.getDefaultInstance(),
			null);
		builder.setApplicationName("musicat");
		builder.setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey));
		return builder.build();
	}
}
