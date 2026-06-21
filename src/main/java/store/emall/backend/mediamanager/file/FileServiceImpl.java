package store.emall.backend.mediamanager.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.dto.*;
import store.emall.backend.mediamanager.file.url.MediaUrlService;
import store.emall.backend.mediamanager.file.util.FileValidation;
import store.emall.backend.mediamanager.file.visibility.FileBindingRepository;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;
import store.emall.backend.mediamanager.folder.Folder;
import store.emall.backend.mediamanager.storage.CloudStorage;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static store.emall.backend.mediamanager.file.util.FileHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final CloudStorage cloudStorage;
    private final FileSpecificationBuilder fileSpecificationBuilder;
    private final FileServiceHelper fileServiceHelper;
    private final FileValidation fileValidation;
    private final MediaUrlService mediaUrlService;
    private final MediaVisibilityService mediaVisibilityService;
    private final FileBindingRepository fileBindingRepository;

    @Value("${media.cache-control.private:private, max-age=300}")
    private String privateCacheControl;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter fileFilter) {
        validateShopScopedFilter(fileFilter);
        Specification<File> spec = fileSpecificationBuilder.build(fileFilter);

        Page<FileDto> page = fileRepository.findAll(spec, pageable)
                .map(mediaUrlService::toDtoWithUrls);

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileDto> getAllFileList(FileFilter fileFilter) {
        validateShopScopedFilter(fileFilter);
        Specification<File> spec = fileSpecificationBuilder.build(fileFilter);

        List<File> files = (spec == null) ? fileRepository.findAll() : fileRepository.findAll(spec);

        return mediaUrlService.toDtosWithUrls(files);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable("fileCache")
    public FileDto getById(UUID id) {
        if (id == null) {
            return null;
        }
        File file = fileServiceHelper.getFile(id);
        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDto getByIdAndScope(UUID id, ScopeType scopeType) {
        if (id == null) {
            return null;
        }
        File file = fileServiceHelper.getFile(id, scopeType);
        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable("fileCache")
    public FileDto getByShopIdAndId(Long shopId, UUID id) {
        if (id == null) {
            return null;
        }
        File file = fileServiceHelper.getFile(id, shopId);
        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional
    public List<FileDto> getByFolderId(Long id) {
        List<File> files = fileRepository.findByFolder_Id(id);
        return mediaUrlService.toDtosWithUrls(files);
    }

    @Override
    public FileDto getAndValidateImage(UUID id, String fieldName) {
        if (id == null) {
            throw FileExceptions.invalidFileType(fieldName);
        }
        FileDto fileDto = getById(id);
        if (!isImage(fileDto.getMimeType())){
            throw FileExceptions.invalidFileType(fieldName);
        }
        return fileDto;
    }

    @Override
    public List<FileDto> getAndValidateImages(List<UUID> ids, String fieldName) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<FileDto> files = getByIds(ids);
        for (FileDto fileDto : files) {
            if (!isImage(fileDto.getMimeType())){
                throw FileExceptions.invalidFileType(fieldName + "[" + fileDto.getId() + "]");
            }
        }
        return files;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return fileRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileDto> getByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<File> files = fileRepository.findAllById(ids);
        return mediaUrlService.toDtosWithUrls(files);
    }

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

    @Override
    @Transactional
    public FileDto rename(FileRenameRequest dto) {
        File file = fileServiceHelper.getFile(dto.getId());

        fileServiceHelper.validateUniqueNameForRename(file, dto.getNewName());

        file.setName(dto.getNewName());
        fileRepository.save(file);

        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional
    public FileDto rename(FileRenameRequest fileRenameRequest, ScopeType scopeType, ManagedByType managedByType) {
        fileServiceHelper.getFile(fileRenameRequest.getId(), scopeType, managedByType);
        return rename(fileRenameRequest);
    }

    @Override
    @Transactional
    public FileDto rename(Long shopId, FileRenameRequest dto) {
        fileServiceHelper.getFile(dto.getId(), shopId);
        return rename(dto);
    }

    @Override
    @Transactional
    public FileDto move(FileMoveRequest dto) {
        File file = fileServiceHelper.getFile(dto.getId());

        if(!Objects.equals(dto.getShopId(), file.getShopId())) {
            throw FileExceptions.shopIdMisMatch();
        }

        Long folderId = file.getFolder() != null ? file.getFolder().getId() : null;
        if (Objects.equals(folderId, dto.getNewFolderId())) {
            return mediaUrlService.toDtoWithUrls(file);
        }

        Folder newFolder = fileServiceHelper.getFolder(dto.getNewFolderId());

        fileServiceHelper.validateUniqueNameForMove(file, dto.getNewFolderId());
        fileServiceHelper.validateFileFolderScope(file.getShopId(), file.getScope(), newFolder);

        file.setFolder(newFolder);
        fileRepository.save(file);

        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional
    public FileDto transfer(FileTransferRequest dto) {
        File file = fileServiceHelper.getFile(dto.getId());
        Folder newFolder = fileServiceHelper.getFolder(dto.getNewFolderId());

        fileServiceHelper.validateTransferTarget(file, newFolder, dto);

        file.setFolder(newFolder);
        file.setShopId(dto.getNewShopId());
        file.setScope(dto.getNewScope());
        file.setManagedBy(dto.getNewManagedBy());

        fileRepository.save(file);

        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        File file = fileServiceHelper.getFile(id);
        delete(file);
    }

    @Override
    @Transactional
    public void delete(Long shopId, UUID id) {
        File file = fileServiceHelper.getFile(id, shopId);
        delete(file);
    }

    @Override
    public void delete(UUID id, ScopeType scopeType, ManagedByType managedByType) {
        File file = fileServiceHelper.getFile(id, scopeType, managedByType);
        delete(file);
    }

    @Override
    @Transactional
    public void deleteByFolderId(long folderId) {
        List<File> files = fileRepository.findByFolder_Id(folderId);
        for (File file : files) {
            delete(file);
        }
    }

    private void delete(File file) {

        fileValidation.validateFileUsage(file.getId());

        deleteKnownKeys(file);
        fileBindingRepository.deleteByFile_Id(file.getId());
        fileRepository.delete(file);
    }

    private void initializePrivateUploadMetadata(File file) {
        file.setVisibility(MediaVisibility.PRIVATE);
        file.setBucket(cloudStorage.getBucketName());
        file.setCacheControl(privateCacheControl);
    }

    private void deleteKnownKeys(File file) {
        for (FileSize size : FileSize.values()) {
            cloudStorage.delete(generateFileKey(file.getId(), size, MediaVisibility.PRIVATE));
            cloudStorage.delete(generateFileKey(file.getId(), size, MediaVisibility.PUBLIC));
            cloudStorage.delete(generateLegacyFileKey(file.getId(), size));
        }
    }

    private void validateShopScopedFilter(FileFilter fileFilter) {
        if (fileFilter == null || fileFilter.getShopId() == null || fileFilter.getFolderId() == null) {
            return;
        }

        fileServiceHelper.getFolder(fileFilter.getFolderId(), fileFilter.getShopId());
    }
}
