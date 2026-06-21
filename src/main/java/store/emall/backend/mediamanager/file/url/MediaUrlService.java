package store.emall.backend.mediamanager.file.url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import store.emall.backend.mediamanager.file.File;
import store.emall.backend.mediamanager.file.FileMapper;
import store.emall.backend.mediamanager.file.FileSize;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.util.FileHelper;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.mediamanager.storage.CloudStorage;

import java.util.List;

import static store.emall.backend.mediamanager.file.util.FileHelper.isImage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaUrlService {

    private final CloudFrontSignerService cloudFrontSignerService;
    private final MediaAuthorizationService mediaAuthorizationService;
    private final CloudStorage cloudStorage;

    @Value("${media.cdn.base-url:}")
    private String cdnBaseUrl;

    public FileDto toDtoWithUrls(File file) {
        if (file == null) {
            return new FileDto();
        }
        return attachUrls(file, FileMapper.toDto(file));
    }

    public List<FileDto> toDtosWithUrls(List<File> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .map(file -> attachUrls(file, FileMapper.toDto(file)))
                .toList();
    }

    private FileDto attachUrls(File file, FileDto dto) {
        log.debug(
                "Resolving media URLs through MediaUrlService fileId={}, visibility={}, mimeType={}, cdnConfigured={}, cloudFrontSigningConfigured={}",
                file.getId(),
                file.getVisibility(),
                file.getMimeType(),
                hasCdnBaseUrl(),
                cloudFrontSignerService.isConfigured()
        );

        dto.setOriginalFileUrl(urlFor(file, FileSize.OPTIMIZED_ORIGINAL));

        if (isImage(file.getMimeType())) {
            dto.setMediumFileUrl(urlFor(file, FileSize.MEDIUM));
            dto.setSmallFileUrl(urlFor(file, FileSize.SMALL));
        }
        return dto;
    }

    private String urlFor(File file, FileSize size) {
        MediaVisibility visibility = file.getVisibility() == null ? MediaVisibility.PRIVATE : file.getVisibility();
        String objectKey = FileHelper.generateFileKey(file.getId(), size, visibility);
        if (objectKey == null || objectKey.isBlank()) {
            log.warn("Media URL skipped because object key is blank fileId={}, size={}, visibility={}", file.getId(), size, visibility);
            return null;
        }

        if (MediaVisibility.PRIVATE.equals(visibility) && !mediaAuthorizationService.canAccess(file)) {
            log.debug("Media URL denied by authorization fileId={}, size={}, objectKey={}", file.getId(), size, objectKey);
            return null;
        }

        if (MediaVisibility.PUBLIC.equals(visibility) && hasCdnBaseUrl()) {
            log.debug("Media URL resolved as unsigned CloudFront URL fileId={}, size={}, objectKey={}", file.getId(), size, objectKey);
            return cdnUrl(objectKey);
        }

        if (hasCdnBaseUrl() && cloudFrontSignerService.isConfigured()) {
            String signedUrl = cloudFrontSignerService.sign(cdnUrl(objectKey));
            if (signedUrl != null) {
                log.debug("Media URL resolved as signed CloudFront URL fileId={}, size={}, objectKey={}", file.getId(), size, objectKey);
                return signedUrl;
            }
            log.warn("CloudFront signing returned null; falling back to S3 presigned URL fileId={}, size={}, objectKey={}", file.getId(), size, objectKey);
        } else {
            log.warn(
                    "CloudFront not configured; falling back to S3 presigned URL fileId={}, size={}, objectKey={}, cdnConfigured={}, cloudFrontSigningConfigured={}",
                    file.getId(),
                    size,
                    objectKey,
                    hasCdnBaseUrl(),
                    cloudFrontSignerService.isConfigured()
            );
        }

        return cloudStorage.generatePresignedUrl(objectKey);
    }

    private boolean hasCdnBaseUrl() {
        return cdnBaseUrl != null && !cdnBaseUrl.isBlank();
    }

    private String cdnUrl(String objectKey) {
        String normalizedBase = cdnBaseUrl.endsWith("/")
                ? cdnBaseUrl.substring(0, cdnBaseUrl.length() - 1)
                : cdnBaseUrl;
        String normalizedKey = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        return normalizedBase + "/" + normalizedKey;
    }
}
