package ps.emall.mediamanager.config.service;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CatalogFeignConfig {
    @Bean
    public RequestInterceptor internalAuthRequestInterceptor(
            @Value("${services.catalog.username}") String username,
            @Value("${services.catalog.password}") String password) {
        return requestTemplate -> {
            String credentials = username + ":" + password;
            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            requestTemplate.header("Authorization", "Basic " + encoded);
        };
    }
}
