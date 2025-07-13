package io.ankoaaaa.mangaRecommendApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedManga {
	private String title;
	private String description;
	private String interpretation;
}