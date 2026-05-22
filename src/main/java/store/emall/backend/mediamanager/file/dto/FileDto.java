package ps.emall.mediamanager.file.dto;

import lombok.*;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.file.Status;

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

    private Long storeId;

    private Status status;

    private ScopeType scope;

    private ManagedByType managedBy;

    private String originalFileUrl;
    private String mediumFileUrl;
    private String smallFileUrl;
}