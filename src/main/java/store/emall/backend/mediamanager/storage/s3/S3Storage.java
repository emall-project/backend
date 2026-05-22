package ps.emall.mediamanager.storage.s3;

import org.springframework.cache.annotation.Cacheable;
import ps.emall.mediamanager.storage.CloudStorage;
import ps.emall.mediamanager.storage.StorageConstant;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

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
    public String generateUrl(String key) {
        // For simplicity, assume public bucket or served via CDN
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }

    @Override
    public String generatePresignedUploadUrl(String key) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(
                        StorageConstant.DEFAULT_PRESIGNEDURL_EXPIRATION_TIME))
                .putObjectRequest(putObjectRequest)
                .build();

        return presigner.presignPutObject(presignRequest)
                .url()
                .toString();
    }

    @Override
    @Cacheable("preSignedCache")
    public String generatePresignedUrl(String key) {
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
}
