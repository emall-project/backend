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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("internal/files")
@PreAuthorize("hasAuthority('ROLE_INTERNAL')")
@RequiredArgsConstructor
public class SystemFileController {

    private final FileService fileService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FileDto>> getAll(Pageable pageable, @ModelAttribute FileFilter fileFilter) {
        PaginatedResponse<FileDto> files = fileService.getAll(pageable, fileFilter);
        return EMallsResponseEntity.ok(files);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FileDto>> getFiles(@ModelAttribute FileFilter fileFilter) {
        List<FileDto> files = fileService.getAllFileList(fileFilter);
        return EMallsResponseEntity.ok(files);
    }


    @GetMapping("/{id}")
    public EMallsResponseEntity<FileDto> getById(@PathVariable UUID id) {
        FileDto file = fileService.getById(id);
        return EMallsResponseEntity.ok(file);
    }

    @GetMapping("/{id}/exists")
    public EMallsResponseEntity<Boolean> fileExists(@PathVariable UUID id) {
        return EMallsResponseEntity.ok(fileService.existsById(id));
    }

    @PostMapping("/list")
    public EMallsResponseEntity<List<FileDto>> getByIds(@RequestBody List<UUID> ids) {
        return EMallsResponseEntity.ok(fileService.getByIds(ids));
    }

    @PostMapping("/complete-upload")
    public EMallsResponseEntity<Void> completeUpload(@RequestBody @Validated CompleteUploadRequest completeUploadRequest) {
        fileService.completeUpload(completeUploadRequest);
        return EMallsResponseEntity.ok(null);
    }

//    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
//    public EMallsResponseEntity<FileDto> upload(@Validated @ModelAttribute FileUploadRequestDto fileUploadRequest) {
//
//        FileDto uploadedFile = fileService.upload(fileUploadRequest);
//        return EMallsResponseEntity.ok(uploadedFile);
//    }


    @PostMapping("/upload-url")
    public EMallsResponseEntity<FileUploadByUrlResponse> uploadByUrl(
            @RequestBody @Validated({Default.class, OnCreate.class}) FileUploadByUrlRequest fileUploadByUrlRequest
    ) {
        fileUploadByUrlRequest.setScope(
                fileUploadByUrlRequest.getStoreId() == null ? ScopeType.SYSTEM : ScopeType.STORE
        );
        fileUploadByUrlRequest.setManagedBy(ManagedByType.SYSTEM);

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

    @PutMapping("/transfer")
    public EMallsResponseEntity<FileDto> transfer(
            @RequestBody @Validated FileTransferRequest fileTransferRequest
    ) {
        FileDto dto = fileService.transfer(fileTransferRequest);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable UUID id) {
        fileService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
