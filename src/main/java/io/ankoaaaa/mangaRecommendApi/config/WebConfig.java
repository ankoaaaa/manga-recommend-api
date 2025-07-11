package io.ankoaaaa.mangaRecommendApi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
    // application.propertiesまたは環境変数から値を取得
    @Value("${CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // すべてのエンドポイントを対象にする
                .allowedOrigins(allowedOrigins) // 環境変数で設定したURLを許可
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}