package ps.emall.mediamanager.folder.dto;

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
public class FolderFilter {
    private String name;
    private Long parentId;
    private Long storeId;
    private ScopeType scope;
    private ManagedByType managedByType;
}
