package ps.emall.mediamanager.file;


import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.mediamanager.common.response.EMallsResponseEntity;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.common.validation.OnTemp;
import ps.emall.mediamanager.file.dto.FileUploadByUrlRequest;
import ps.emall.mediamanager.file.dto.FileUploadByUrlResponse;
import ps.emall.mediamanager.file.util.FileValidation;
import ps.emall.mediamanager.folder.FolderService;
import ps.emall.mediamanager.folder.SystemFolder;

import java.util.UUID;

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
