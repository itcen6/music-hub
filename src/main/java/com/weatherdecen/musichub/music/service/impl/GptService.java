package com.weatherdecen.musichub.music.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherdecen.musichub.common.dto.ChatGPTRequest;
import com.weatherdecen.musichub.common.dto.enums.OpenAiPrompt;
import com.weatherdecen.musichub.common.exception.CustomException;
import com.weatherdecen.musichub.common.exception.ErrorCode;
import com.weatherdecen.musichub.music.dto.MusicFromGpt;
import com.weatherdecen.musichub.music.service.IGptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


/**
 * Gpt Service
 *
 * @since 2025-05-24
 * @author 전하은
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class GptService implements IGptService {

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${OPENAI_MODEL}")
    private String openAiModel;

    @Qualifier("openAiTemplate")
    private final RestTemplate openAiTemplate;
    private final ObjectMapper objectMapper;

    public List<MusicFromGpt> getRecommendMusicsFromGpt(String prompt) {
        String rawJsonResponse = getChatGPTRawJson(getRecommendPrompt(prompt));

        try {
            // 응답 구조: { choices: [ { message: { content: "{ \"songs\": [...] }" } } ] }
            JsonNode root = objectMapper.readTree(rawJsonResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            content = content.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode songList = objectMapper.readTree(content).path("songs");
            return objectMapper.readerForListOf(MusicFromGpt.class).readValue(songList.toString());
        } catch (Exception e) {
            log.error("Failed to parse music list from GPT: {}", e.getMessage());
            throw new CustomException(ErrorCode.JSON_PASING_ERROR, Map.of("gptRawJson", rawJsonResponse), msg->log.error("gpt value 값을 json으로 파싱중 에러 발생"), e);
        }
    }

    private String getChatGPTRawJson(String prompt) {
        ChatGPTRequest request = ChatGPTRequest.of(prompt, openAiModel);

        return openAiTemplate.postForObject(apiURL, request, String.class);
    }

    private static String getRecommendPrompt(String prompt) {
        return OpenAiPrompt.RECOMMEND_MUSIC.getText().replace("{emotion}", prompt);
    }
}
