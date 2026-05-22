package ps.emall.mediamanager.file.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileTransferRequest {

    @NotNull(message = "file.id.notNull")
    private UUID id;

    @NotNull(message = "file.newFolderId.notNull")
    private Long newFolderId;

    private Long newStoreId;

    @NotNull(message = "file.newScope.notNull")
    private ScopeType newScope;

    @NotNull(message = "file.newManagedBy.notNull")
    private ManagedByType newManagedBy;
}
