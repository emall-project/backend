package store.emall.backend.mediamanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import store.emall.backend.mediamanager.config.FeignConfig;

import java.util.TimeZone;

@EnableFeignClients(defaultConfiguration = FeignConfig.class)
public class MediaManagerApplication {

    private static final String APPLICATION_TIME_ZONE = "Asia/Gaza";

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(APPLICATION_TIME_ZONE));
        SpringApplication.run(MediaManagerApplication.class, args);
    }

}
