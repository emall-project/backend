package store.emall.backend.mediamanager.storage.local;

import store.emall.backend.mediamanager.storage.CloudStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

public class LocalStorage implements CloudStorage {

    private final Path basePath;

    public LocalStorage() {
        this.basePath = Paths.get("./uploads"); // Configurable if needed
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create local storage directory", e);
        }
    }

    @Override
    public String upload(String key, InputStream data, String contentType) {
        try {
            Path filePath = basePath.resolve(key);
            Files.createDirectories(filePath.getParent());
            try (OutputStream os = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                data.transferTo(os);
            }
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file locally", e);
        }
    }

    @Override
    public InputStream download(String key) {
        try {
            Path filePath = basePath.resolve(key);
            return Files.newInputStream(filePath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read local file", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path filePath = basePath.resolve(key);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete local file", e);
        }
    }

    @Override
    public String copy(String sourceKey, String destinationKey, String contentType, String cacheControl) {
        try {
            Path sourcePath = basePath.resolve(sourceKey);
            Path destinationPath = basePath.resolve(destinationKey);
            Files.createDirectories(destinationPath.getParent());
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            return destinationKey;
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy local file", e);
        }
    }

    @Override
    public String generateUrl(String key) {
        // Returns a local file path or could return a URL served by Spring Boot
        return basePath.resolve(key).toAbsolutePath().toUri().toString();
    }

    @Override
    public String generatePresignedUrl(String key) {
        return generateUrl(key);
    }

    @Override
    public String generatePresignedUploadUrl(String key) {
        return "";
    }

    @Override
    public boolean fileExist(String key) {
        return Files.exists(basePath.resolve(key));
    }

    @Override
    public String getBucketName() {
        return "local";
    }
}
