package store.emall.backend.mediamanager.folder;

import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.mediamanager.folder.dto.FolderDto;
import store.emall.backend.mediamanager.folder.dto.FolderFilter;

import java.util.List;

@RestController
@RequestMapping("/internal/folders")
@PreAuthorize("hasAuthority('ROLE_INTERNAL')")
@RequiredArgsConstructor
public class SystemFolderController {

    private final FolderService folderService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FolderDto>> getAll(
            Pageable pageable,
            @ModelAttribute FolderFilter filter
    ) {
        PaginatedResponse<FolderDto> folders = folderService.getAll(pageable, filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FolderDto>> getFolderList(@ModelAttribute FolderFilter filter) {
        List<FolderDto> folders = folderService.getAllFolderList(filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FolderDto> getById(@PathVariable @Positive Long id) {
        FolderDto folder = folderService.getById(id);
        return EMallsResponseEntity.ok(folder);
    }

    @PostMapping
    public EMallsResponseEntity<FolderDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) FolderDto folderDto
    ) {
        folderDto.setScope(folderDto.getShopId() == null ? ScopeType.SYSTEM : ScopeType.SHOP);
        folderDto.setManagedBy(ManagedByType.SYSTEM);

        FolderDto dto = folderService.create(folderDto);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping()
    public EMallsResponseEntity<FolderDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) FolderDto folderDto
    ) {
        FolderDto dto = folderService.systemUpdate(folderDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        folderService.systemDelete(id);
        return EMallsResponseEntity.noContent(null);
    }

}
