package store.emall.backend.mediamanager.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import store.emall.backend.mediamanager.storage.StorageConstant;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableCaching
@EnableScheduling
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("preSignedCache");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(StorageConstant.DEFAULT_PRESIGNEDURL_EXPIRATION_TIME - 100, TimeUnit.SECONDS) // TTL per entry
        );
        return cacheManager;
    }

}
