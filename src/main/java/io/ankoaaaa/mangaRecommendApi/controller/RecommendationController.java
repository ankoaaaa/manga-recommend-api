package io.ankoaaaa.mangaRecommendApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.ankoaaaa.mangaRecommendApi.dto.RecommendationRequest;
import io.ankoaaaa.mangaRecommendApi.dto.RecommendationResponse;
import io.ankoaaaa.mangaRecommendApi.service.GeminiApiService;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

	@Autowired
	private GeminiApiService geminiApiService;

	@PostMapping
	public RecommendationResponse getRecommendations(@RequestBody RecommendationRequest request) { // 戻り値を一旦Stringに
		System.out.println("受け取ったタイトル: " + request.getTitles());
		return geminiApiService.getRecommendation(request.getTitles());
	}
}