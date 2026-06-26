package store.emall.backend.mediamanager.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.FileFilter;
import store.emall.backend.mediamanager.file.dto.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MediaQueryService mediaQueryService;
    private final MediaValidationService mediaValidationService;
    private final MediaCommandService mediaCommandService;
    private final MediaUploadService mediaUploadService;


    public PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter spec) {
        return mediaQueryService.getAll(pageable, spec);
    }

    public List<FileDto> getAllFileList(FileFilter fileFilter) {
        return mediaQueryService.getAllFileList(fileFilter);
    }

    public FileDto getById(UUID id) {
        return mediaQueryService.getById(id);
    }

    public FileDto getByIdAndScope(UUID id, ScopeType scopeType) {
        return mediaQueryService.getByIdAndScope(id, scopeType);
    }

    public FileDto getByShopIdAndId(Long shopId, UUID id) {
        return mediaQueryService.getByShopIdAndId(shopId, id);
    }

    public List<FileDto> getByFolderId(Long id) {
        return mediaQueryService.getByFolderId(id);
    }

    public Map<UUID, FileDto> getMedia(List<UUID> imageIds) {
        return mediaQueryService.getMedia(imageIds);
    }

    public boolean existsById(UUID id) {
        return mediaQueryService.existsById(id);
    }

    public List<FileDto> getByIds(List<UUID> ids) {
        return mediaQueryService.getByIds(ids);
    }

    public FileDto getAndValidateImage(UUID id, String fieldName) {
        FileDto fileDto = mediaQueryService.getById(id);
        mediaValidationService.requireImage(fileDto, fieldName);
        return fileDto;
    }

    public List<FileDto> getAndValidateImages(List<UUID> ids, String fieldName) {
        List<FileDto> files = mediaQueryService.getByIds(ids);
        mediaValidationService.requireImages(files, fieldName);
        return files;
    }

    // public FileDto upload(FileUploadRequestDto fileUploadRequest){


    public FileUploadByUrlResponse uploadByUrl(Long shopId, FileUploadByUrlRequest fileUploadByUrlRequest) {
        return mediaUploadService.uploadByUrl(shopId, fileUploadByUrlRequest);
    }

    public FileUploadByUrlResponse uploadByUrl(FileUploadByUrlRequest fileUploadByUrlRequest) {
        return mediaUploadService.uploadByUrl(fileUploadByUrlRequest);
    }

    public void completeUpload(CompleteUploadRequest completeUploadRequest) {
        mediaUploadService.completeUpload(completeUploadRequest);
    }

    public FileDto rename(FileRenameRequest fileRenameRequest) {
        return mediaCommandService.rename(fileRenameRequest);
    }

    public FileDto rename(FileRenameRequest fileRenameRequest, ScopeType scopeType, ManagedByType managedByType) {
        return mediaCommandService.rename(fileRenameRequest, scopeType, managedByType);
    }

    public FileDto rename(Long shopId, FileRenameRequest fileRenameRequest) {
        return mediaCommandService.rename(shopId, fileRenameRequest);
    }

    public FileDto move(FileMoveRequest fileMoveRequest) {
        return mediaCommandService.move(fileMoveRequest);
    }

    public FileDto transfer(FileTransferRequest dto) {
        return mediaCommandService.transfer(dto);
    }

    public void delete(UUID id) {
        mediaCommandService.delete(id);
    }

    public void delete(Long shopId, UUID id) {
        mediaCommandService.delete(shopId, id);
    }

    public void delete(UUID id, ScopeType scopeType, ManagedByType managedByType) {
        mediaCommandService.delete(id, scopeType, managedByType);
    }

    public void deleteByFolderId(long folderId) {
        mediaCommandService.deleteByFolderId(folderId);
    }

}
