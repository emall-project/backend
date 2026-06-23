package store.emall.backend.mediamanager.file.service;

import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.dto.FileMoveRequest;
import store.emall.backend.mediamanager.file.dto.FileRenameRequest;
import store.emall.backend.mediamanager.file.dto.FileTransferRequest;

import java.util.UUID;

public interface MediaCommandService {

    FileDto rename(FileRenameRequest request);

    FileDto rename(FileRenameRequest fileRenameRequest, ScopeType scopeType, ManagedByType managedByType);

    FileDto rename(Long shopId, FileRenameRequest request);

    FileDto move(FileMoveRequest request);

    FileDto transfer(FileTransferRequest request);

    void delete(UUID id);

    void delete(Long shopId, UUID id);

    void delete(UUID id, ScopeType scopeType, ManagedByType managedByType);

    void deleteByFolderId(long folderId);
}
