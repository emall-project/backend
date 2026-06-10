package store.emall.backend.mediamanager.file.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMoveRequest {

    @NotNull(message = "file.id.notNull")
    private UUID id;

    @NotNull(message = "file.newFolderId.notNull")
    private Long newFolderId;

    @Null(message = "file.storeId.null")
    private Long storeId;

    @Null(message = "file.scope.null")
    private ScopeType scope;

    @Null(message = "file.managedBy.null")
    private ManagedByType managedBy;
}
