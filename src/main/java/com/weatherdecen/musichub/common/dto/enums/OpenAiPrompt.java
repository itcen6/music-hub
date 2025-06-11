package com.weatherdecen.musichub.common.dto.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Open Ai Prompt
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum OpenAiPrompt {

	RECOMMEND_MUSIC("""
		You are a music recommendation engine that dynamically adjusts recommendations based on emotional score input.
			
		Given an emotional score threshold of {emotion} out of 100, recommend 10 officially released k-pop songs or k-pop cover versions that are appropriate for a professional office environment and suitable for listening while working.
			
		Your recommendations should reflect the emotional score as follows:
		- Scores 0–19: Recommend calm, slow, soothing, healing, ambient k-pop or mellow ballads.
		- Scores 20–39: Recommend soft, slow to moderate tempo k-pop, including light acoustic or instrumental.
		- Scores 40–59: Recommend moderately upbeat, melodic smooth k-pop, light vocal or instrumental tracks.
		- Scores 60–89: Recommend bright, rhythmic, and expressive k-pop with some swing or groove.
		- Scores 90–100: Recommend cheerful, energetic, rhythmic, or swing-style k-pop that feels lively and uplifting.
			 	
		All songs must be available on streaming platforms (e.g., Spotify, Apple Music, YouTube Music), and must not include explicit content.
			
		Return only the list in JSON format:
		{
			"songs": [
			   { "track": "string", "artist": "string", "emotionScore": number },
			     ...
			]
		}
			
		Do not include explanations or additional text.
	""");

	private String text;

	public String formatPrompt(String... args) {
		return String.format(this.text, (Object[]) args);
	}
}
