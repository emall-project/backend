package store.emall.backend.mediamanager.file.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRequestDto {
    @NotNull(message = "file.file.notNull")
    private MultipartFile file;

    @NotNull(message = "file.folderId.notnull")
    private Long folderId;

    private Long shopId;
}
