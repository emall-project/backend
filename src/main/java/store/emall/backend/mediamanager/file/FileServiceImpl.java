package store.emall.backend.mediamanager.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.dto.*;
import store.emall.backend.mediamanager.file.util.FileValidation;
import store.emall.backend.mediamanager.folder.Folder;
import store.emall.backend.mediamanager.storage.CloudStorage;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter fileFilter) {
        validateStoreScopedFilter(fileFilter);
        Specification<File> spec = fileSpecificationBuilder.build(fileFilter);

        Page<FileDto> page = fileRepository.findAll(spec, pageable)
                .map(FileMapper::toDto)
                .map(fileDto -> injectPresignedUrlToTheDto(fileDto, false, cloudStorage));

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileDto> getAllFileList(FileFilter fileFilter) {
        validateStoreScopedFilter(fileFilter);
        Specification<File> spec = fileSpecificationBuilder.build(fileFilter);

        List<File> files = (spec == null) ? fileRepository.findAll() : fileRepository.findAll(spec);

        return files.stream()
                .map(FileMapper::toDto)
                .map(fileDto -> injectPresignedUrlToTheDto(fileDto, false, cloudStorage))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable("fileCache")
    public FileDto getById(UUID id) {
        File file = fileServiceHelper.getFile(id);
        return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDto getByIdAndScope(UUID id, ScopeType scopeType) {
        File file = fileServiceHelper.getFile(id, scopeType);
        return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable("fileCache")
    public FileDto getByShopIdAndId(Long shopId, UUID id) {
        File file = fileServiceHelper.getFile(id, shopId);
        return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
    }

    @Override
    @Transactional
    public List<FileDto> getByFolderId(Long id) {
        List<File> files = fileRepository.findByFolder_Id(id);
        return files.stream()
                .map(FileMapper::toDto)
                .map(fileDto -> injectPresignedUrlToTheDto(fileDto, false, cloudStorage))
                .collect(Collectors.toList());
    }

    @Override
    public FileDto getAndValidateImage(UUID id, String fieldName) {
        FileDto fileDto = getById(id);
        if (!isImage(fileDto.getMimeType())){
            throw FileExceptions.invalidFileType(fieldName);
        }
        return fileDto;
    }

    @Override
    public List<FileDto> getAndValidateImages(List<UUID> ids, String fieldName) {
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
        List<File> files = fileRepository.findAllById(ids);
        return files.stream()
                .map(FileMapper::toDto)
                .map(fileDto -> injectPresignedUrlToTheDto(fileDto, false, cloudStorage))
                .collect(Collectors.toList());
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

        fileRepository.save(file);

        String fileKey = generateFileKey(fileId, FileSize.ORIGINAL);
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

        fileRepository.save(file);

        String fileKey = generateFileKey(fileId, FileSize.ORIGINAL);
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

        fileRepository.save(file);
    }

    @Override
    @Transactional
    public FileDto rename(FileRenameRequest dto) {
        File file = fileServiceHelper.getFile(dto.getId());

        fileServiceHelper.validateUniqueNameForRename(file, dto.getNewName());

        file.setName(dto.getNewName());
        fileRepository.save(file);

        return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
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
            return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
        }

        Folder newFolder = fileServiceHelper.getFolder(dto.getNewFolderId());

        fileServiceHelper.validateUniqueNameForMove(file, dto.getNewFolderId());
        fileServiceHelper.validateFileFolderScope(file.getShopId(), file.getScope(), newFolder);

        file.setFolder(newFolder);
        fileRepository.save(file);

        return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
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

        return injectPresignedUrlToTheDto(FileMapper.toDto(file), false, cloudStorage);
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
        fileRepository.delete(file);

        cloudStorage.delete(generateFileKey(file.getId(), FileSize.ORIGINAL));
        cloudStorage.delete(generateFileKey(file.getId(), FileSize.OPTIMIZED_ORIGINAL));
        if (!isImage(file.getMimeType())) return;

        cloudStorage.delete(generateFileKey(file.getId(), FileSize.MEDIUM));
        cloudStorage.delete(generateFileKey(file.getId(), FileSize.SMALL));
    }

    private void validateStoreScopedFilter(FileFilter fileFilter) {
        if (fileFilter == null || fileFilter.getShopId() == null || fileFilter.getFolderId() == null) {
            return;
        }

        fileServiceHelper.getFolder(fileFilter.getFolderId(), fileFilter.getShopId());
    }
}
