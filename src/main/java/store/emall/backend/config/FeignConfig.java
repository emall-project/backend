package store.emall.backend.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor internalAuthRequestInterceptor(
            @Value("${internal.auth.username}") String username,
            @Value("${internal.auth.password}") String password) {
        return requestTemplate -> {
            String credentials = username + ":" + password;
            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            requestTemplate.header("Authorization", "Basic " + encoded);
        };
    }
}