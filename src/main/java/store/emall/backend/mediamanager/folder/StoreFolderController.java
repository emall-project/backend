package ps.emall.mediamanager.folder;

import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.mediamanager.common.page.PaginatedResponse;
import ps.emall.mediamanager.common.response.EMallsResponseEntity;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.common.validation.OnCreate;
import ps.emall.mediamanager.common.validation.OnUpdate;
import ps.emall.mediamanager.folder.dto.FolderDto;
import ps.emall.mediamanager.folder.dto.FolderFilter;

import java.util.List;

@RestController
@RequestMapping("/stores/{storeId}/folders")
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#storeId)")
@RequiredArgsConstructor
public class StoreFolderController {

    private final FolderService folderService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FolderDto>> getAll(
            @PathVariable Long storeId,
            Pageable pageable,
            @ModelAttribute FolderFilter filter
    ) {
        filter.setStoreId(storeId);
        filter.setScope(ScopeType.STORE);

        PaginatedResponse<FolderDto> folders = folderService.getAll(pageable, filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FolderDto>> getFolderList(
            @PathVariable Long storeId,
            @ModelAttribute FolderFilter filter
    ) {
        filter.setStoreId(storeId);
        filter.setScope(ScopeType.STORE);

        List<FolderDto> folders = folderService.getAllFolderList(filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FolderDto> getById(
            @PathVariable Long storeId,
            @PathVariable @Positive Long id
    ) {
        FolderDto folder = folderService.getByStoreIdAndId(storeId, id);
        return EMallsResponseEntity.ok(folder);
    }

    @PostMapping
    public EMallsResponseEntity<FolderDto> create(
            @PathVariable Long storeId,
            @RequestBody @Validated({Default.class, OnCreate.class}) FolderDto folderDto
    ) {
        folderDto.setStoreId(storeId);
        folderDto.setScope(ScopeType.STORE);
        folderDto.setManagedBy(ManagedByType.STORE);

        FolderDto dto = folderService.storeCreate(folderDto);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    public EMallsResponseEntity<FolderDto> update(
            @PathVariable Long storeId,
            @RequestBody @Validated({Default.class, OnUpdate.class}) FolderDto folderDto
    ) {
        folderDto.setStoreId(storeId);
        folderDto.setScope(ScopeType.STORE);
        folderDto.setManagedBy(ManagedByType.STORE);

        FolderDto dto = folderService.storeUpdate(folderDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(
            @PathVariable Long storeId,
            @PathVariable @Positive Long id
    ) {
        folderService.storeDelete(storeId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
