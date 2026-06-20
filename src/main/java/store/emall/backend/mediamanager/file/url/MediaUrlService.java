package store.emall.backend.mediamanager.file.url;

import lombok.RequiredArgsConstructor;
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
            return null;
        }

        if (MediaVisibility.PRIVATE.equals(visibility) && !mediaAuthorizationService.canAccess(file)) {
            return null;
        }

        if (MediaVisibility.PUBLIC.equals(visibility) && hasCdnBaseUrl()) {
            return cdnUrl(objectKey);
        }

        if (hasCdnBaseUrl() && cloudFrontSignerService.isConfigured()) {
            String signedUrl = cloudFrontSignerService.sign(cdnUrl(objectKey));
            if (signedUrl != null) {
                return signedUrl;
            }
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
