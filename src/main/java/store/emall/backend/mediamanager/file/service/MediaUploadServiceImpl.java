package store.emall.backend.mediamanager.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.*;
import store.emall.backend.mediamanager.file.dto.CompleteUploadRequest;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlRequest;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlResponse;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;
import store.emall.backend.mediamanager.folder.Folder;
import store.emall.backend.mediamanager.storage.CloudStorage;

import java.util.UUID;

import static store.emall.backend.mediamanager.file.util.FileHelper.generateFileKey;

@Service
@RequiredArgsConstructor
public class MediaUploadServiceImpl implements  MediaUploadService {

    private final FileRepository fileRepository;
    private final CloudStorage cloudStorage;
    private final FileServiceHelper fileServiceHelper;
    private final MediaVisibilityService mediaVisibilityService;

    @Value("${media.cache-control.private:private, max-age=300}")
    private String privateCacheControl;


    @Override
    @Transactional
    public FileUploadByUrlResponse uploadByUrl(FileUploadByUrlRequest dto) {
        UUID fileId = UUID.randomUUID();

        fileServiceHelper.validateScopeConsistency(dto);

        Folder folder = fileServiceHelper.getFolder(dto.getFolderId());
        fileServiceHelper.validateFileFolderScope(dto.getShopId(), dto.getScope(), folder);
        fileServiceHelper.validateUniqueName(dto.getFolderId(), dto.getName());

        File file = FileMapper.toEntity(dto, folder);
        file.setId(fileId);
        file.setStatus(Status.PENDING);
        initializePrivateUploadMetadata(file);

        fileRepository.save(file);

        String fileKey = generateFileKey(fileId, FileSize.ORIGINAL, MediaVisibility.PRIVATE);
        String uploadUrl = cloudStorage.generatePresignedUploadUrl(fileKey);

        return FileUploadByUrlResponse.builder()
                .fileId(fileId)
                .uploadUrl(uploadUrl)
                .build();
    }

    @Override
    @Transactional
    public FileUploadByUrlResponse uploadByUrl(Long shopId, FileUploadByUrlRequest dto) {
        dto.setShopId(shopId);
        dto.setScope(ScopeType.SHOP);
        dto.setManagedBy(ManagedByType.SHOP);

        Folder folder = fileServiceHelper.getFolder(dto.getFolderId(), shopId);

        fileServiceHelper.validateScopeConsistency(dto);
        fileServiceHelper.validateFileFolderScope(dto.getShopId(), dto.getScope(), folder);
        fileServiceHelper.validateUniqueName(dto.getFolderId(), dto.getName());

        UUID fileId = UUID.randomUUID();

        File file = FileMapper.toEntity(dto, folder);
        file.setId(fileId);
        file.setStatus(Status.PENDING);
        initializePrivateUploadMetadata(file);

        fileRepository.save(file);

        String fileKey = generateFileKey(fileId, FileSize.ORIGINAL, MediaVisibility.PRIVATE);
        String uploadUrl = cloudStorage.generatePresignedUploadUrl(fileKey);

        return FileUploadByUrlResponse.builder()
                .fileId(fileId)
                .uploadUrl(uploadUrl)
                .build();
    }

    @Override
    public FileUploadByUrlResponse createTempUploadUrl(FileUploadByUrlRequest request) {
        return null;
    }

    @Override
    @Transactional
    public void completeUpload(CompleteUploadRequest completeUploadRequest) {
        File file = fileServiceHelper.getFile(completeUploadRequest.getId());

        file.setStatus(completeUploadRequest.getStatus());
        file.setErrorMessage(completeUploadRequest.getErrorMessage());
        file.setSize(completeUploadRequest.getSize());
        file.setMimeType(completeUploadRequest.getMimeType());
        file.setExtension(completeUploadRequest.getExtension());
        file.setContentType(completeUploadRequest.getMimeType());
        file.setCacheControl(privateCacheControl);

        fileRepository.save(file);
        if (completeUploadRequest.getStatus() == Status.APPROVED) {
            mediaVisibilityService.recomputeVisibility(file.getId());
        }
    }



    private void initializePrivateUploadMetadata(File file) {
        file.setVisibility(MediaVisibility.PRIVATE);
        file.setBucket(cloudStorage.getBucketName());
        file.setCacheControl(privateCacheControl);
    }
}
