package ps.emall.mediamanager.file;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.mediamanager.common.page.PaginatedResponse;
import ps.emall.mediamanager.common.response.EMallsResponseEntity;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.common.validation.OnCreate;
import ps.emall.mediamanager.file.dto.*;

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
                fileUploadByUrlRequest.getStoreId() == null ? ScopeType.SYSTEM : ScopeType.STORE
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
