package store.emall.backend.mediamanager.file;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.mediamanager.file.dto.*;
import store.emall.backend.mediamanager.file.service.FileService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("admin/files")
@PreAuthorize("@auth.isAdmin()")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileService fileService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FileDto>> getAll(Pageable pageable, @ModelAttribute FileFilter fileFilter) {
        fileFilter.setScope(ScopeType.SYSTEM);
        PaginatedResponse<FileDto> files = fileService.getAll(pageable, fileFilter);
        return EMallsResponseEntity.ok(files);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FileDto>> getFiles(@ModelAttribute FileFilter fileFilter) {
        fileFilter.setScope(ScopeType.SYSTEM);
        List<FileDto> files = fileService.getAllFileList(fileFilter);
        return EMallsResponseEntity.ok(files);
    }


    @GetMapping("/{id}")
    public EMallsResponseEntity<FileDto> getById(@PathVariable UUID id) {
        FileDto file = fileService.getByIdAndScope(id, ScopeType.SYSTEM);
        return EMallsResponseEntity.ok(file);
    }


    @PostMapping("/upload-url")
    public EMallsResponseEntity<FileUploadByUrlResponse> uploadByUrl(
            @RequestBody @Validated({Default.class, OnCreate.class}) FileUploadByUrlRequest fileUploadByUrlRequest
    ) {
        fileUploadByUrlRequest.setScope(
                fileUploadByUrlRequest.getShopId() == null ? ScopeType.SYSTEM : ScopeType.SHOP
        );
        fileUploadByUrlRequest.setManagedBy(ManagedByType.ADMIN);

        return EMallsResponseEntity.ok(fileService.uploadByUrl(fileUploadByUrlRequest));
    }


    @PutMapping("/rename")
    public EMallsResponseEntity<FileDto> rename(@RequestBody FileRenameRequest fileRenameRequest) {
        FileDto dto = fileService.rename(fileRenameRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @PutMapping("/move")
    public EMallsResponseEntity<FileDto> move(@RequestBody @Validated FileMoveRequest fileMoveRequest) {
        FileDto dto = fileService.move(fileMoveRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable UUID id) {
        fileService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
