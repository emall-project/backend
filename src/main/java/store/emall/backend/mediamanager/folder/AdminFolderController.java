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
@RequestMapping("/admin/folders")
@PreAuthorize("@auth.isAdmin()")
@RequiredArgsConstructor
public class AdminFolderController {

    private final FolderService folderService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FolderDto>> getAll(
            Pageable pageable,
            @ModelAttribute FolderFilter filter
    ) {
        filter.setScope(ScopeType.SYSTEM);
        PaginatedResponse<FolderDto> folders = folderService.getAll(pageable, filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FolderDto>> getFolderList(@ModelAttribute FolderFilter filter) {
        filter.setScope(ScopeType.SYSTEM);
        List<FolderDto> folders = folderService.getAllFolderList(filter);
        return EMallsResponseEntity.ok(folders);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FolderDto> getById(@PathVariable @Positive Long id) {
        FolderDto folder = folderService.getByIdAndScope(id, ScopeType.SYSTEM);
        return EMallsResponseEntity.ok(folder);
    }

    @PostMapping
    public EMallsResponseEntity<FolderDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) FolderDto folderDto
    ) {
        folderDto.setStoreId(null);
        folderDto.setScope(ScopeType.SYSTEM);
        folderDto.setManagedBy(ManagedByType.ADMIN);

        FolderDto dto = folderService.create(folderDto);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping()
    public EMallsResponseEntity<FolderDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) FolderDto folderDto
    ) {
        folderDto.setStoreId(null);
        folderDto.setScope(ScopeType.SYSTEM);
        folderDto.setManagedBy(ManagedByType.ADMIN);

        FolderDto dto = folderService.adminUpdate(folderDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        folderService.adminDelete(id, ScopeType.SYSTEM, ManagedByType.ADMIN);
        return EMallsResponseEntity.noContent(null);
    }
}
