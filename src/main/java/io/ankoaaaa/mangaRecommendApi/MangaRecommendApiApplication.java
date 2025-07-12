package io.ankoaaaa.mangaRecommendApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MangaRecommendApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MangaRecommendApiApplication.class, args);
	}

}
