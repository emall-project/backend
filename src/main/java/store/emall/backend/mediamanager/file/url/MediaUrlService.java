package store.emall.backend.mediamanager.file.url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final MediaCdnProvider cdnProvider;
    private final MediaAuthorizationService mediaAuthorizationService;
    private final CloudStorage cloudStorage;

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
                "Resolving media URLs through MediaUrlService fileId={}, visibility={}, mimeType={}, cdnProvider={}, cdnConfigured={}",
                file.getId(),
                file.getVisibility(),
                file.getMimeType(),
                cdnProvider.providerName(),
                cdnProvider.isConfigured()
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
        String fileKey = FileHelper.generateFileKey(file.getId(), size, visibility);
        if (fileKey == null || fileKey.isBlank()) {
            log.warn("Media URL skipped because object key is blank fileId={}, size={}, visibility={}", file.getId(), size, visibility);
            return null;
        }

        if (MediaVisibility.PRIVATE.equals(visibility) && !mediaAuthorizationService.canAccess(file)) {
            log.debug("Media URL denied by authorization fileId={}, size={}, fileKey={}", file.getId(), size, fileKey);
            return null;
        }

        if (MediaVisibility.PUBLIC.equals(visibility) && cdnProvider.isConfigured()) {
            log.debug(
                    "Media URL resolved as unsigned CDN URL fileId={}, size={}, fileKey={}, cdnProvider={}",
                    file.getId(),
                    size,
                    fileKey,
                    cdnProvider.providerName()
            );
            return cdnProvider.publicUrl(fileKey);
        }

        if (cdnProvider.isConfigured()) {
            var signedUrl = cdnProvider.signedUrl(fileKey);
            if (signedUrl.isPresent()) {
                log.debug(
                        "Media URL resolved as signed CDN URL fileId={}, size={}, fileKey={}, cdnProvider={}",
                        file.getId(),
                        size,
                        fileKey,
                        cdnProvider.providerName()
                );
                return signedUrl.get();
            }
            log.warn(
                    "CDN signing returned empty; falling back to S3 presigned URL fileId={}, size={}, fileKey={}, cdnProvider={}",
                    file.getId(),
                    size,
                    fileKey,
                    cdnProvider.providerName()
            );
        } else {
            log.warn(
                    "CDN provider not configured; falling back to S3 presigned URL fileId={}, size={}, fileKey={}, cdnProvider={}",
                    file.getId(),
                    size,
                    fileKey,
                    cdnProvider.providerName()
            );
        }

        return cloudStorage.generatePresignedUrl(fileKey);
    }
}
