package store.emall.backend.config;

import feign.RequestInterceptor;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

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

    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(
                60, TimeUnit.SECONDS,
                120, TimeUnit.SECONDS,
                true
        );
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }
}
