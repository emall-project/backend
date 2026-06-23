package store.emall.backend.mediamanager.file.service;

import org.springframework.data.domain.Pageable;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.FileFilter;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MediaQueryService {
    FileDto getById(UUID id);

    FileDto getByIdAndScope(UUID id, ScopeType scopeType);

    FileDto getByShopIdAndId(Long shopId, UUID id);

    List<FileDto> getByIds(List<UUID> ids);

    Map<UUID, FileDto> getMedia(List<UUID> ids);

    List<FileDto> getByFolderId(Long folderId);

    boolean existsById(UUID id);

    PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter filter);

    List<FileDto> getAllFileList(FileFilter filter);
}
