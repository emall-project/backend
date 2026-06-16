package store.emall.backend.mediamanager.file.dto;

import lombok.*;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.Status;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {

    private UUID id;

    private String name;

    private Long folderId;

    private String mimeType;

    private String extension;

    private Long size;

    private Long shopId;

    private Status status;

    private ScopeType scope;

    private ManagedByType managedBy;

    private String originalFileUrl;
    private String mediumFileUrl;
    private String smallFileUrl;
}