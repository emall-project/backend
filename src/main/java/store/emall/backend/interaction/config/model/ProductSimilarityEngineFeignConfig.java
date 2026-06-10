package store.emall.backend.interaction.config.model;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ProductSimilarityEngineFeignConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(
            @Value("${models.product-similarity-engine.username}") String username,
            @Value("${models.product-similarity-engine.password}") String password
    ) {
        return new BasicAuthRequestInterceptor(username, password);
    }
}
