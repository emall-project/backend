package store.emall.backend.mediamanager.file.visibility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.mediamanager.file.File;
import store.emall.backend.mediamanager.file.FileRepository;
import store.emall.backend.mediamanager.file.FileSize;
import store.emall.backend.mediamanager.file.util.FileHelper;
import store.emall.backend.mediamanager.storage.CloudStorage;

import java.util.List;

import static store.emall.backend.mediamanager.file.util.FileHelper.isImage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaObjectPromotionService {

    private final CloudStorage cloudStorage;
    private final FileRepository fileRepository;

    @Value("${media.cache-control.public:public, max-age=31536000, immutable}")
    private String publicCacheControl;

    @Value("${media.cache-control.private:private, max-age=300}")
    private String privateCacheControl;

    @Transactional
    public void promote(File file) {
        if (file == null || file.getId() == null) {
            return;
        }

        try {
            for (FileSize size : expectedSizes(file)) {
                copyIfSourceExists(
                        key(file, size, MediaVisibility.PRIVATE),
                        key(file, size, MediaVisibility.PUBLIC),
                        contentTypeFor(file, size),
                        publicCacheControl
                );
            }

            assertRequiredObjectsExist(file, MediaVisibility.PUBLIC);

            file.setVisibility(MediaVisibility.PUBLIC);
            file.setBucket(cloudStorage.getBucketName());
            file.setCacheControl(publicCacheControl);
            fileRepository.save(file);
        } catch (Exception e) {
            log.warn("Media promotion failed for fileId={}: {}", file.getId(), e.getMessage());
        }
    }

    @Transactional
    public void demote(File file) {
        if (file == null || file.getId() == null) {
            return;
        }

        try {
            for (FileSize size : expectedSizes(file)) {
                String privateKey = key(file, size, MediaVisibility.PRIVATE);
                if (!cloudStorage.fileExist(privateKey)) {
                    copyIfSourceExists(
                            key(file, size, MediaVisibility.PUBLIC),
                            privateKey,
                            contentTypeFor(file, size),
                            privateCacheControl
                    );
                }
            }

            assertRequiredObjectsExist(file, MediaVisibility.PRIVATE);

            file.setVisibility(MediaVisibility.PRIVATE);
            file.setBucket(cloudStorage.getBucketName());
            file.setCacheControl(privateCacheControl);
            fileRepository.save(file);

            deletePublicCopies(file);
        } catch (Exception e) {
            log.warn("Media demotion failed for fileId={}: {}", file.getId(), e.getMessage());
        }
    }

    private void copyIfSourceExists(String sourceKey, String destinationKey, String contentType, String cacheControl) {
        if (cloudStorage.fileExist(sourceKey)) {
            cloudStorage.copy(sourceKey, destinationKey, contentType, cacheControl);
        }
    }

    private void assertRequiredObjectsExist(File file, MediaVisibility visibility) {
        for (FileSize size : requiredSizes(file)) {
            String objectKey = key(file, size, visibility);
            if (!cloudStorage.fileExist(objectKey)) {
                throw new IllegalStateException("Required media object is missing: " + objectKey);
            }
        }
    }

    private void deletePublicCopies(File file) {
        for (FileSize size : expectedSizes(file)) {
            try {
                cloudStorage.delete(key(file, size, MediaVisibility.PUBLIC));
            } catch (Exception e) {
                log.warn("Failed to delete public media object fileId={} size={}: {}", file.getId(), size, e.getMessage());
            }
        }
    }

    private List<FileSize> expectedSizes(File file) {
        if (isImage(file.getMimeType())) {
            return List.of(
                    FileSize.ORIGINAL,
                    FileSize.OPTIMIZED_ORIGINAL,
                    FileSize.MEDIUM,
                    FileSize.SMALL
            );
        }
        return List.of(FileSize.ORIGINAL, FileSize.OPTIMIZED_ORIGINAL);
    }

    private List<FileSize> requiredSizes(File file) {
        if (isImage(file.getMimeType())) {
            return List.of(FileSize.OPTIMIZED_ORIGINAL, FileSize.MEDIUM, FileSize.SMALL);
        }
        return List.of(FileSize.OPTIMIZED_ORIGINAL);
    }

    private String key(File file, FileSize size, MediaVisibility visibility) {
        return FileHelper.generateFileKey(file.getId(), size, visibility);
    }

    private String contentTypeFor(File file, FileSize size) {
        if (isImage(file.getMimeType()) && size != FileSize.ORIGINAL) {
            return "image/webp";
        }
        return file.getContentType() != null ? file.getContentType() : file.getMimeType();
    }
}
