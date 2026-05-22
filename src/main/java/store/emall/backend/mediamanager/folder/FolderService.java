package ps.emall.mediamanager.folder;

import org.springframework.data.domain.Pageable;
import ps.emall.mediamanager.common.page.PaginatedResponse;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.folder.dto.FolderDto;
import ps.emall.mediamanager.folder.dto.FolderFilter;

import java.util.List;

public interface FolderService {

    PaginatedResponse<FolderDto> getAll(Pageable pageable, FolderFilter filter);

    List<FolderDto> getAllFolderList(FolderFilter filter);

    FolderDto getById(Long id);

    FolderDto getByStoreIdAndId(Long storeId, Long id);

    FolderDto getByIdAndScope(Long id, ScopeType scope);

    FolderDto create(FolderDto folderDto);

    FolderDto storeCreate(FolderDto folderDto);

    FolderDto systemUpdate(FolderDto folderDto);

    FolderDto adminUpdate(FolderDto dto);

    FolderDto storeUpdate(FolderDto folderDto);

    void systemDelete(Long id);

    void adminDelete(Long id, ScopeType scope, ManagedByType managedBy);

    void storeDelete(Long storeId, Long id);

    Long getSystemFolderId(SystemFolder systemFolder);
}