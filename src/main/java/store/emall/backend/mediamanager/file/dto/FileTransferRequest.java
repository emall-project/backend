package store.emall.backend.mediamanager.file.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileTransferRequest {

    @NotNull(message = "file.id.notNull")
    private UUID id;

    @NotNull(message = "file.newFolderId.notNull")
    private Long newFolderId;

    private Long newShopId;

    @NotNull(message = "file.newScope.notNull")
    private ScopeType newScope;

    @NotNull(message = "file.newManagedBy.notNull")
    private ManagedByType newManagedBy;
}
