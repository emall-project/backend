package store.emall.backend.mediamanager.file;


import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.common.validation.OnTemp;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlRequest;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlResponse;
import store.emall.backend.mediamanager.folder.FolderService;
import store.emall.backend.mediamanager.folder.SystemFolder;

@Slf4j
@RestController
@RequestMapping("temps/files")
@RequiredArgsConstructor
public class TempFileController {

    private final FileService fileService;
    private final FolderService folderService;

    @PostMapping("/upload-url")
    public EMallsResponseEntity<FileUploadByUrlResponse> uploadByUrl(
            @RequestBody @Validated({Default.class, OnTemp.class}) FileUploadByUrlRequest fileUploadByUrlRequest
    ) {
        Long folderId = folderService.getSystemFolderId(SystemFolder.TEMP_FOLDER);
        fileUploadByUrlRequest.setFolderId(folderId);
        fileUploadByUrlRequest.setStoreId(null);
        fileUploadByUrlRequest.setScope(ScopeType.SYSTEM);
        fileUploadByUrlRequest.setManagedBy(ManagedByType.SYSTEM);
        return EMallsResponseEntity.ok(fileService.uploadByUrl(fileUploadByUrlRequest));
    }

}
