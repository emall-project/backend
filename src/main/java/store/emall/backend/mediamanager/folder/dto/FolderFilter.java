package store.emall.backend.mediamanager.folder.dto;

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
public class FolderFilter {
    private String name;
    private Long parentId;
    private Long storeId;
    private ScopeType scope;
    private ManagedByType managedByType;
}
