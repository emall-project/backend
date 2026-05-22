package ps.emall.mediamanager.file;

import org.springframework.data.domain.Pageable;
import ps.emall.mediamanager.common.page.PaginatedResponse;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.file.dto.*;

import java.util.List;
import java.util.UUID;

public interface FileService {

    PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter spec);

    List<FileDto> getAllFileList(FileFilter fileFilter);

    FileDto getById(UUID id);

    FileDto getByIdAndScope(UUID id, ScopeType scopeType);

    FileDto getByStoreIdAndId(Long storeId, UUID id);

    List<FileDto> getByFolderId(Long id);

    boolean existsById(UUID id);

    List<FileDto> getByIds(List<UUID> ids);

//    FileDto upload(FileUploadRequestDto fileUploadRequest);

    FileUploadByUrlResponse uploadByUrl(Long storeId, FileUploadByUrlRequest fileUploadByUrlRequest);

    FileUploadByUrlResponse uploadByUrl(FileUploadByUrlRequest fileUploadByUrlRequest);

    void completeUpload(CompleteUploadRequest completeUploadRequest);

    FileDto rename(FileRenameRequest fileRenameRequest);

    FileDto rename(FileRenameRequest fileRenameRequest, ScopeType scopeType, ManagedByType  managedByType);

    FileDto rename(Long storeId, FileRenameRequest fileRenameRequest);

    FileDto move(FileMoveRequest fileMoveRequest);

    FileDto transfer(FileTransferRequest dto);

    void delete(UUID id);

    void delete(Long storeId, UUID id);

    void delete(UUID id, ScopeType scopeType,  ManagedByType managedByType);

    void deleteByFolderId(long folderId);

}
