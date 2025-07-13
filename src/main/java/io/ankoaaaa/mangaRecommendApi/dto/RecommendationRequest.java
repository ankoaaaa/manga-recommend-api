package io.ankoaaaa.mangaRecommendApi.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecommendationRequest {
	private List<String> titles;
	private String reason;
}