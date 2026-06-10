package store.emall.backend.interaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import store.emall.backend.interaction.config.FeignConfig;

@SpringBootApplication
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
public class InteractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteractionApplication.class, args);
    }

}
