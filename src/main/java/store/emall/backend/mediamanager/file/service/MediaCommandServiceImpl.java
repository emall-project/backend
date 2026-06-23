package store.emall.backend.mediamanager.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.*;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.dto.FileMoveRequest;
import store.emall.backend.mediamanager.file.dto.FileRenameRequest;
import store.emall.backend.mediamanager.file.dto.FileTransferRequest;
import store.emall.backend.mediamanager.file.url.MediaUrlService;
import store.emall.backend.mediamanager.file.visibility.FileBindingDto;
import store.emall.backend.mediamanager.file.visibility.FileBindingRepository;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;
import store.emall.backend.mediamanager.folder.Folder;
import store.emall.backend.mediamanager.storage.CloudStorage;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static store.emall.backend.mediamanager.file.util.FileHelper.generateFileKey;
import static store.emall.backend.mediamanager.file.util.FileHelper.generateLegacyFileKey;

@Service
@RequiredArgsConstructor
public class MediaCommandServiceImpl implements MediaCommandService {

    private final FileRepository fileRepository;
    private final CloudStorage cloudStorage;
    private final FileServiceHelper fileServiceHelper;
    private final MediaUrlService mediaUrlService;
    private final FileBindingRepository fileBindingRepository;
    private final MediaVisibilityService mediaVisibilityService;

    @Value("${media.cache-control.private:private, max-age=300}")
    private String privateCacheControl;


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

        if (!Objects.equals(dto.getShopId(), file.getShopId())) {
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
        List<FileBindingDto> bindings = mediaVisibilityService.getFileBindings(file.getId());
        if(bindings.size() > 0){
            throw FileExceptions.fileInUse(bindings);
        }

        deleteKnownKeys(file);
        fileBindingRepository.deleteByFile_Id(file.getId());
        fileRepository.delete(file);
    }


    private void deleteKnownKeys(File file) {
        for (FileSize size : FileSize.values()) {
            cloudStorage.delete(generateFileKey(file.getId(), size, MediaVisibility.PRIVATE));
            cloudStorage.delete(generateFileKey(file.getId(), size, MediaVisibility.PUBLIC));
            cloudStorage.delete(generateLegacyFileKey(file.getId(), size));
        }
    }
}
