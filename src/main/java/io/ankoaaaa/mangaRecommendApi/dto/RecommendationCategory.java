package io.ankoaaaa.mangaRecommendApi.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecommendationCategory {
	private String categoryTitle; // 例: 「同じ世界の他の物語」
	private List<RecommendedManga> recommendations; // おすすめ漫画のリスト
}