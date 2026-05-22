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
@RequestMapping("stores/{storeId}/files")
//todo: replace with isShopOwnerOf(#storeId)
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#storeId)")
@RequiredArgsConstructor
public class StoreFileController {

    private final FileService fileService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FileDto>> getAll(
            @PathVariable Long storeId,
            Pageable pageable,
            @ModelAttribute FileFilter fileFilter
    ) {
        fileFilter.setStoreId(storeId);
        fileFilter.setScope(ScopeType.STORE);
        PaginatedResponse<FileDto> files = fileService.getAll(pageable, fileFilter);
        return EMallsResponseEntity.ok(files);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FileDto>> getFiles(
            @PathVariable Long storeId,
            @ModelAttribute FileFilter fileFilter
    ) {
        fileFilter.setStoreId(storeId);
        fileFilter.setScope(ScopeType.STORE);
        List<FileDto> files = fileService.getAllFileList(fileFilter);
        return EMallsResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FileDto> getById(@PathVariable Long storeId, @PathVariable UUID id) {
        FileDto file = fileService.getByStoreIdAndId(storeId, id);
        return EMallsResponseEntity.ok(file);
    }

    @PostMapping("/upload-url")
    public EMallsResponseEntity<FileUploadByUrlResponse> uploadByUrl(
            @PathVariable Long storeId,
            @RequestBody @Validated({Default.class, OnCreate.class}) FileUploadByUrlRequest fileUploadByUrlRequest
    ) {
        return EMallsResponseEntity.ok(fileService.uploadByUrl(storeId, fileUploadByUrlRequest));
    }

    @PutMapping("/rename")
    public EMallsResponseEntity<FileDto> rename(
            @PathVariable Long storeId,
            @RequestBody FileRenameRequest fileRenameRequest
    ) {
        FileDto dto = fileService.rename(storeId, fileRenameRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @PutMapping("/move")
    public EMallsResponseEntity<FileDto> move(
            @PathVariable Long storeId,
            @RequestBody @Validated FileMoveRequest fileMoveRequest
    ) {
        fileMoveRequest.setStoreId(storeId);

        FileDto dto = fileService.move(fileMoveRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long storeId, @PathVariable UUID id) {
        fileService.delete(storeId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
