package store.emall.backend.mediamanager.folder;

import org.springframework.data.domain.Pageable;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.folder.dto.FolderDto;
import store.emall.backend.mediamanager.folder.dto.FolderFilter;

import java.util.List;

public interface FolderService {

    PaginatedResponse<FolderDto> getAll(Pageable pageable, FolderFilter filter);

    List<FolderDto> getAllFolderList(FolderFilter filter);

    FolderDto getById(Long id);

    FolderDto getByShopIdAndId(Long shopId, Long id);

    FolderDto getByIdAndScope(Long id, ScopeType scope);

    FolderDto create(FolderDto folderDto);

    FolderDto shopCreate(FolderDto folderDto);

    FolderDto systemUpdate(FolderDto folderDto);

    FolderDto adminUpdate(FolderDto dto);

    FolderDto shopUpdate(FolderDto folderDto);

    void systemDelete(Long id);

    void adminDelete(Long id, ScopeType scope, ManagedByType managedBy);

    void shopDelete(Long shopId, Long id);

    Long getSystemFolderId(SystemFolder systemFolder);
}