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
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.mediamanager.file.dto.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("shops/{shopId}/files")
//todo: replace with isShopOwnerOf(#shopId)
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
@RequiredArgsConstructor
public class ShopFileController {

    private final FileService fileService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FileDto>> getAll(
            @PathVariable Long shopId,
            Pageable pageable,
            @ModelAttribute FileFilter fileFilter
    ) {
        fileFilter.setShopId(shopId);
        fileFilter.setScope(ScopeType.SHOP);
        PaginatedResponse<FileDto> files = fileService.getAll(pageable, fileFilter);
        return EMallsResponseEntity.ok(files);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FileDto>> getFiles(
            @PathVariable Long shopId,
            @ModelAttribute FileFilter fileFilter
    ) {
        fileFilter.setShopId(shopId);
        fileFilter.setScope(ScopeType.SHOP);
        List<FileDto> files = fileService.getAllFileList(fileFilter);
        return EMallsResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FileDto> getById(@PathVariable Long shopId, @PathVariable UUID id) {
        FileDto file = fileService.getByShopIdAndId(shopId, id);
        return EMallsResponseEntity.ok(file);
    }

    @PostMapping("/upload-url")
    public EMallsResponseEntity<FileUploadByUrlResponse> uploadByUrl(
            @PathVariable Long shopId,
            @RequestBody @Validated({Default.class, OnCreate.class}) FileUploadByUrlRequest fileUploadByUrlRequest
    ) {
        return EMallsResponseEntity.ok(fileService.uploadByUrl(shopId, fileUploadByUrlRequest));
    }

    @PutMapping("/rename")
    public EMallsResponseEntity<FileDto> rename(
            @PathVariable Long shopId,
            @RequestBody FileRenameRequest fileRenameRequest
    ) {
        FileDto dto = fileService.rename(shopId, fileRenameRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @PutMapping("/move")
    public EMallsResponseEntity<FileDto> move(
            @PathVariable Long shopId,
            @RequestBody @Validated FileMoveRequest fileMoveRequest
    ) {
        fileMoveRequest.setShopId(shopId);

        FileDto dto = fileService.move(fileMoveRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long shopId, @PathVariable UUID id) {
        fileService.delete(shopId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
