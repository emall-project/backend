package ps.emall.mediamanager.folder;

import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.common.page.PaginatedResponse;
import ps.emall.mediamanager.common.response.EMallsResponseEntity;
import ps.emall.mediamanager.common.validation.OnCreate;
import ps.emall.mediamanager.common.validation.OnUpdate;
import ps.emall.mediamanager.folder.dto.FolderDto;
import ps.emall.mediamanager.folder.dto.FolderFilter;

import java.util.List;
import java.util.Locale;

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
        folderDto.setScope(folderDto.getStoreId() == null ? ScopeType.SYSTEM : ScopeType.STORE);
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
