package io.ankoaaaa.mangaRecommendApi.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.ankoaaaa.mangaRecommendApi.constants.PromptConstants;
import io.ankoaaaa.mangaRecommendApi.dto.RecommendationRequest;
import io.ankoaaaa.mangaRecommendApi.dto.RecommendationResponse;

@Service
public class GeminiApiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final int MAX_RETRIES = 3;
	private static final long INITIAL_BACKOFF_MS = 1000;

	@Cacheable("recommendations")
	public RecommendationResponse getRecommendation(RecommendationRequest request) {
		// 1. プロンプトを組み立てる
		String finalPrompt = PromptConstants.buildFinalPrompt(request.getTitles(), request.getReason());
		System.out.println("最終プロンプト: " + finalPrompt);

		// 2. AIへリクエストを送信する
		String rawResponse = postToGeminiApi(finalPrompt);

		// 3. レスポンスをパースする
		try {
			return parseResponse(rawResponse);
		} catch (JsonProcessingException e) {
			System.err.println("JSON Parse Error: " + e.getMessage());
			throw new RuntimeException("Failed to parse AI response", e);
		}
	}

	/**
	 * Gemini APIにリクエストを送信し、生のレスポンス文字列を取得する（リトライ処理込み）
	 */
	private String postToGeminiApi(String prompt) {
		String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" +
				apiKey;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, Object> requestBody = Map.of(
				"contents",
				List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
				requestBody,
				headers);

		long backoff = INITIAL_BACKOFF_MS;
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				return restTemplate.postForObject(url, entity, String.class);
			} catch (HttpServerErrorException.ServiceUnavailable e) {
				System.err.println(
						"503 Service Unavailable. Retrying in " +
								backoff +
								" ms. (Attempt " +
								(i + 1) +
								")");
				if (i == MAX_RETRIES - 1) {
					throw e;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(backoff);
				} catch (InterruptedException interruptedException) {
					Thread.currentThread().interrupt();
				}
				backoff *= 2;
			}
		}
		throw new RuntimeException("Failed to get response from AI after multiple retries.");
	}

	/**
	 * AIからの生のレスポンス（JSON文字列）をパースして、RecommendationResponseオブジェクトに変換する
	 */
	private RecommendationResponse parseResponse(String rawResponse)
			throws JsonProcessingException {
		JsonNode rootNode = objectMapper.readTree(rawResponse);
		String jsonText = rootNode
				.path("candidates")
				.get(0)
				.path("content")
				.path("parts")
				.get(0)
				.path("text")
				.asText();

		jsonText = jsonText.trim().replace("```json", "").replace("```", "").trim();

		System.out.println("AIへの問い合わせ成功");
		return objectMapper.readValue(jsonText, RecommendationResponse.class);
	}
}