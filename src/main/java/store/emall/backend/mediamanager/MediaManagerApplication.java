package ps.emall.mediamanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ps.emall.mediamanager.config.FeignConfig;

import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
public class MediaManagerApplication {

    private static final String APPLICATION_TIME_ZONE = "Asia/Gaza";

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(APPLICATION_TIME_ZONE));
        SpringApplication.run(MediaManagerApplication.class, args);
    }

}
