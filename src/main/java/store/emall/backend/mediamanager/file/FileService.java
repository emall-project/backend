package store.emall.backend.mediamanager.file;

import org.springframework.data.domain.Pageable;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.dto.*;

import java.util.List;
import java.util.UUID;

public interface FileService {

    PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter spec);

    List<FileDto> getAllFileList(FileFilter fileFilter);

    FileDto getById(UUID id);

    FileDto getByIdAndScope(UUID id, ScopeType scopeType);

    FileDto getByShopIdAndId(Long shopId, UUID id);

    List<FileDto> getByFolderId(Long id);

    FileDto getAndValidateImage(UUID id,  String fieldName);

    List<FileDto> getAndValidateImages(List<UUID> ids, String fieldName);

    boolean existsById(UUID id);

    List<FileDto> getByIds(List<UUID> ids);

//    FileDto upload(FileUploadRequestDto fileUploadRequest);

    FileUploadByUrlResponse uploadByUrl(Long shopId, FileUploadByUrlRequest fileUploadByUrlRequest);

    FileUploadByUrlResponse uploadByUrl(FileUploadByUrlRequest fileUploadByUrlRequest);

    void completeUpload(CompleteUploadRequest completeUploadRequest);

    FileDto rename(FileRenameRequest fileRenameRequest);

    FileDto rename(FileRenameRequest fileRenameRequest, ScopeType scopeType, ManagedByType  managedByType);

    FileDto rename(Long shopId, FileRenameRequest fileRenameRequest);

    FileDto move(FileMoveRequest fileMoveRequest);

    FileDto transfer(FileTransferRequest dto);

    void delete(UUID id);

    void delete(Long shopId, UUID id);

    void delete(UUID id, ScopeType scopeType,  ManagedByType managedByType);

    void deleteByFolderId(long folderId);

}
