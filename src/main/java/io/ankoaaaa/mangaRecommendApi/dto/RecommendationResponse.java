package io.ankoaaaa.mangaRecommendApi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
	private String introduction;
	private List<RecommendationCategory> categories;
}