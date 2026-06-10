package store.emall.backend.mediamanager.file.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.emall.backend.mediamanager.file.Status;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteUploadRequest {
    @NotNull(message = "file.id.notnull")
    private UUID id;

    @NotNull(message = "file.status.notnull")
    private Status status;

    private String errorMessage;

    @NotNull(message = "file.size.notnull")
    private Long size;

    @NotNull(message = "file.mimeType.notnull")
    private String mimeType;

    @NotNull(message = "file.mimeType.notnull")
    private String extension;
}
