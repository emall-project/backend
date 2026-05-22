package ps.emall.mediamanager.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.file.dto.FileMoveRequest;
import ps.emall.mediamanager.file.dto.FileTransferRequest;
import ps.emall.mediamanager.file.dto.FileUploadByUrlRequest;
import ps.emall.mediamanager.folder.Folder;
import ps.emall.mediamanager.folder.FolderExceptions;
import ps.emall.mediamanager.folder.FolderRepository;

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

    public File getFile(UUID id, Long storeId) {
        return fileRepository.findByStoreIdAndId(storeId, id)
                .orElseThrow(FileExceptions::fileNotFound);
    }

    public Folder getFolder(Long id) {
        return folderRepository.findById(id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long id, Long storeId) {
        return folderRepository.findByStoreIdAndId(storeId, id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public void validateScopeConsistency(FileUploadByUrlRequest dto) {
        if (dto.getScope() == null) {
            throw FileExceptions.scopeNotFound();
        }

        if (dto.getManagedBy() == null) {
            throw FileExceptions.managedByNotFound();
        }

        if (dto.getScope() == ScopeType.SYSTEM && dto.getStoreId() != null) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (dto.getScope() == ScopeType.STORE && dto.getStoreId() == null) {
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

        if (dto.getScope() == ScopeType.SYSTEM && dto.getStoreId() != null) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (dto.getScope() == ScopeType.STORE && dto.getStoreId() == null) {
            throw FileExceptions.fileScopeMismatch();
        }
    }

    public void validateFileFolderScope(Long fileStoreId, ScopeType fileScope, Folder folder) {
        if (folder.getScope() != fileScope) {
            throw FileExceptions.fileScopeMismatch();
        }

        if (!Objects.equals(fileStoreId, folder.getStoreId())) {
            throw FileExceptions.storeIdMisMatch();
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

        if (!Objects.equals(newFolder.getStoreId(), dto.getNewStoreId())) {
            throw FileExceptions.storeIdMisMatch();
        }

        boolean sameScope = file.getScope() == dto.getNewScope();
        boolean sameStore = Objects.equals(file.getStoreId(), dto.getNewStoreId());
        boolean sameFolder = Objects.equals(file.getFolder().getId(), dto.getNewFolderId());
        boolean sameManager = file.getManagedBy() == dto.getNewManagedBy();

        if (!(sameScope && sameStore && sameFolder && sameManager)
                && fileRepository.existsByNameAndFolder_Id(file.getName(), dto.getNewFolderId())) {
            throw FileExceptions.fileNameExists();
        }
    }
}