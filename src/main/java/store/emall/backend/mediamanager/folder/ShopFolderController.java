package store.emall.backend.mediamanager.folder;

import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.mediamanager.folder.dto.FolderDto;
import store.emall.backend.mediamanager.folder.dto.FolderFilter;

import java.util.List;

@RestController
@RequestMapping("/shops/{shopId}/folders")
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
@RequiredArgsConstructor
public class ShopFolderController {

    private final FolderService folderService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FolderDto>> getAll(
            @PathVariable Long shopId,
            Pageable pageable,
            @ModelAttribute FolderFilter filter
    ) {
        filter.setShopId(shopId);
        filter.setScope(ScopeType.SHOP);

        PaginatedResponse<FolderDto> folders = folderService.getAll(pageable, filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FolderDto>> getFolderList(
            @PathVariable Long shopId,
            @ModelAttribute FolderFilter filter
    ) {
        filter.setShopId(shopId);
        filter.setScope(ScopeType.SHOP);

        List<FolderDto> folders = folderService.getAllFolderList(filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FolderDto> getById(
            @PathVariable Long shopId,
            @PathVariable @Positive Long id
    ) {
        FolderDto folder = folderService.getByShopIdAndId(shopId, id);
        return EMallsResponseEntity.ok(folder);
    }

    @PostMapping
    public EMallsResponseEntity<FolderDto> create(
            @PathVariable Long shopId,
            @RequestBody @Validated({Default.class, OnCreate.class}) FolderDto folderDto
    ) {
        folderDto.setShopId(shopId);
        folderDto.setScope(ScopeType.SHOP);
        folderDto.setManagedBy(ManagedByType.SHOP);

        FolderDto dto = folderService.shopCreate(folderDto);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    public EMallsResponseEntity<FolderDto> update(
            @PathVariable Long shopId,
            @RequestBody @Validated({Default.class, OnUpdate.class}) FolderDto folderDto
    ) {
        folderDto.setShopId(shopId);
        folderDto.setScope(ScopeType.SHOP);
        folderDto.setManagedBy(ManagedByType.SHOP);

        FolderDto dto = folderService.shopUpdate(folderDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(
            @PathVariable Long shopId,
            @PathVariable @Positive Long id
    ) {
        folderService.shopDelete(shopId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
