package ps.emall.mediamanager.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileFilter {
    private String name;

    private String mimeType;

    private Long folderId;

    private Long storeId;

    private Long fileSize;

    private Status status;

    private ScopeType scope;

    private ManagedByType managedBy;
}
