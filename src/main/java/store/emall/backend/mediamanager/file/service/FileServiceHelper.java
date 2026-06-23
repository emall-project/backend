package store.emall.backend.mediamanager.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.File;
import store.emall.backend.mediamanager.file.FileExceptions;
import store.emall.backend.mediamanager.file.FileRepository;
import store.emall.backend.mediamanager.file.dto.FileMoveRequest;
import store.emall.backend.mediamanager.file.dto.FileTransferRequest;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlRequest;
import store.emall.backend.mediamanager.folder.Folder;
import store.emall.backend.mediamanager.folder.FolderExceptions;
import store.emall.backend.mediamanager.folder.FolderRepository;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceHelper {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    public File getFile(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(FileExceptions::fileNotFound);
    }
    public File getFile(UUID id, ScopeType scopeType) {
        return fileRepository.findByIdAndScope(id, scopeType)
                .orElseThrow(FileExceptions::fileNotFound);
    }
    public File getFile(UUID id, ScopeType scopeType, ManagedByType managedByType) {
        return fileRepository.findByIdAndScopeAndManagedBy(id, scopeType, managedByType)
                .orElseThrow(FileExceptions::fileNotFound);
    }

    public File getFile(UUID id, Long shopId) {
        return fileRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(FileExceptions::fileNotFound);
    }

    public Folder getFolder(Long id) {
        return folderRepository.findById(id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long id, Long shopId) {
        return folderRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public void validateScopeConsistency(FileUploadByUrlRequest dto) {
        if (dto.getScope() == null) {
            throw FileExceptions.scopeNotFound();
        }

        if (dto.getManagedBy() == null) {
            throw FileExceptions.managedByNotFound();
        }

        if (dto.getScope() == ScopeType.SYSTEM && dto.getShopId() != null) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (dto.getScope() == ScopeType.SHOP && dto.getShopId() == null) {
            throw FileExceptions.fileScopeMismatch();
        }
    }

    public void validateScopeConsistency(FileMoveRequest dto) {
        if (dto.getScope() == null) {
            throw FileExceptions.scopeNotFound();
        }

        if (dto.getManagedBy() == null) {
            throw FileExceptions.managedByNotFound();
        }

        if (dto.getScope() == ScopeType.SYSTEM && dto.getShopId() != null) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (dto.getScope() == ScopeType.SHOP && dto.getShopId() == null) {
            throw FileExceptions.fileScopeMismatch();
        }
    }

    public void validateFileFolderScope(Long fileShopId, ScopeType fileScope, Folder folder) {
        if (folder.getScope() != fileScope) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (!Objects.equals(fileShopId, folder.getShopId())) {
            throw FileExceptions.shopIdMisMatch();
        }
    }

    public void validateUniqueName(Long folderId, String name) {
        if (fileRepository.existsByNameAndFolder_Id(name, folderId)) {
            throw FileExceptions.fileNameExists();
        }
    }

    public void validateUniqueNameForRename(File file, String newName) {
        boolean sameName = Objects.equals(file.getName(), newName);
        if (!sameName && fileRepository.existsByNameAndFolder_Id(newName, file.getFolder().getId())) {
            throw FileExceptions.fileNameExists();
        }
    }

    public void validateUniqueNameForMove(File file, Long newFolderId) {
        boolean sameFolder = Objects.equals(file.getFolder().getId(), newFolderId);
        if (!sameFolder && fileRepository.existsByNameAndFolder_Id(file.getName(), newFolderId)) {
            throw FileExceptions.fileNameExists();
        }
    }

    public void validateTransferTarget(File file, Folder newFolder, FileTransferRequest dto) {
        if (newFolder.getScope() != dto.getNewScope()) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (!Objects.equals(newFolder.getShopId(), dto.getNewShopId())) {
            throw FileExceptions.shopIdMisMatch();
        }

        boolean sameScope = file.getScope() == dto.getNewScope();
        boolean sameShop = Objects.equals(file.getShopId(), dto.getNewShopId());
        boolean sameFolder = Objects.equals(file.getFolder().getId(), dto.getNewFolderId());
        boolean sameManager = file.getManagedBy() == dto.getNewManagedBy();

        if (!(sameScope && sameShop && sameFolder && sameManager)
                && fileRepository.existsByNameAndFolder_Id(file.getName(), dto.getNewFolderId())) {
            throw FileExceptions.fileNameExists();
        }
    }
}