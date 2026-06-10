package store.emall.backend.mediamanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.emall.backend.mediamanager.storage.CloudStorage;
import store.emall.backend.mediamanager.storage.local.LocalStorage;
import store.emall.backend.mediamanager.storage.s3.S3Storage;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class StorageConfig {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;

    public StorageConfig(S3Client s3Client, S3Presigner presigner,
                         @Value("${aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.bucket = bucket;
    }

    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "s3")
    public CloudStorage s3Storage() {
        return new S3Storage(s3Client, presigner, bucket);
    }

    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
    public CloudStorage localStorage() {
        return new LocalStorage();
    }
}
