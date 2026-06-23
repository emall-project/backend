package store.emall.backend.mediamanager.storage.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import store.emall.backend.mediamanager.storage.CloudStorage;
import store.emall.backend.mediamanager.storage.StorageConstant;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

@Slf4j
public class S3Storage implements CloudStorage {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;

    public S3Storage(S3Client s3Client, S3Presigner presigner, String bucket) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.bucket = bucket;
    }

    @Override
    public String upload(String key, InputStream data, String contentType) {
        try {
            byte[] bytes = data.readAllBytes();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
            // FUTURE WORK: replace this with "fromInputStream(data, size)"  to save memory
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3" + e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return s3Client.getObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }


    @Override
    public boolean fileExist(String key) {
        try {
            s3Client.headObject(b -> b.bucket(bucket).key(key));
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw new RuntimeException("Error checking file existence: " + key, e);
        }
    }


    @Override
    public void delete(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    @Override
    public String copy(String sourceKey, String destinationKey, String contentType, String cacheControl) {
        try {
            CopyObjectRequest.Builder request = CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucket)
                    .destinationKey(destinationKey)
                    .metadataDirective(MetadataDirective.REPLACE);

            if (contentType != null && !contentType.isBlank()) {
                request.contentType(contentType);
            }
            if (cacheControl != null && !cacheControl.isBlank()) {
                request.cacheControl(cacheControl);
            }

            s3Client.copyObject(request.build());
            return destinationKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy file in S3 from " + sourceKey + " to " + destinationKey, e);
        }
    }

    @Override
    public String generateUrl(String key) {
        return generatePresignedUrl(key);
    }

    @Override
    public String generatePresignedUploadUrl(String key) {
        log.debug("Generating S3 presigned upload URL for key={}", key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(
                        StorageConstant.DEFAULT_PRESIGNEDURL_EXPIRATION_TIME))
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = presigner.presignPutObject(presignRequest);
        return presignedPutObjectRequest.url()
                .toString();
    }

    @Override
    @Cacheable("preSignedCache")
    public String generatePresignedUrl(String key) {
        log.debug("Generating S3 presigned download URL for key={}", key);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(StorageConstant.DEFAULT_PRESIGNEDURL_EXPIRATION_TIME))
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    @Override
    public String getBucketName() {
        return bucket;
    }
}
