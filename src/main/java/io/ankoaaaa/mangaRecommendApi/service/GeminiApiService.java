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

import io.ankoaaaa.mangaRecommendApi.dto.RecommendationResponse;

@Service
public class GeminiApiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	// リトライ回数と待ち時間の設定
	private static final int MAX_RETRIES = 3; // 最大リトライ回数
	private static final long INITIAL_BACKOFF_MS = 1000; // 初回の待ち時間（1秒）

	@Cacheable("recommendations")
	public RecommendationResponse getRecommendation(List<String> titles) {
		String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
				+ apiKey;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String prompt = String.join("、", titles)
				+ "のような漫画が好きです。この人が好きそうな漫画を2つ、それぞれ50字程度の紹介文をつけて、以下のフォーマットのJSON形式で返してください。\n"
				+ "フォーマット: {\"recommendations\": [{\"title\": \"漫画のタイトル\", \"description\": \"紹介文\"}]}";

		Map<String, Object> requestBody = Map.of(
				"contents", List.of(
						Map.of("parts", List.of(
								Map.of("text", prompt)))));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		long backoff = INITIAL_BACKOFF_MS;
		// リトライ処理のループ
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {

				// AIからの生の応答（文字列）を取得する部分は同じ
				String rawResponse = restTemplate.postForObject(url, entity, String.class);

				// 1. 生の応答（文字列）をJSONノードにパース
				JsonNode rootNode = objectMapper.readTree(rawResponse);

				// 2. 深い階層から目的のテキスト部分を掘り出す
				String jsonText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text")
						.asText();

				// 3. 不要なMarkdownマーカーを掃除
				jsonText = jsonText.trim().replace("```json", "").replace("```", "").trim();

				System.out.println("AIへの問い合わせ成功");

				// 4. 掃除したJSON文字列を、最終的なRecommendationResponseオブジェクトに変換
				return objectMapper.readValue(jsonText, RecommendationResponse.class);
			} catch (HttpServerErrorException.ServiceUnavailable e) {
				// 503エラーをキャッチした場合
				System.err
						.println("503 Service Unavailable. Retrying in " + backoff + " ms. (Attempt " + (i + 1) + ")");
				if (i == MAX_RETRIES - 1) {
					// 最後のリトライでも失敗した場合は、例外をスローする
					throw e;
				}
				try {
					// 待ち時間を挟む
					TimeUnit.MILLISECONDS.sleep(backoff);
				} catch (InterruptedException interruptedException) {
					Thread.currentThread().interrupt();
				}
				// 次の待ち時間を2倍にする
				backoff *= 2;
			} catch (JsonProcessingException e) {
				// JSONのパースに失敗した場合のエラー処理
				System.err.println("JSON Parse Error: " + e.getMessage());
				throw new RuntimeException("Failed to parse AI response", e);
			}
		}
		// ループが正常に完了することは通常ないが、念のためnullを返す
		return null;
	}
}