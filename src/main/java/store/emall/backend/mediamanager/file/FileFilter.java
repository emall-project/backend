package store.emall.backend.mediamanager.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileFilter {
    private String name;

    private String mimeType;

    private Long folderId;

    private Long shopId;

    private Long fileSize;

    private Status status;

    private ScopeType scope;

    private ManagedByType managedBy;
}
