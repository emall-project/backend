package ps.emall.mediamanager.file.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.common.validation.OnCreate;
import ps.emall.mediamanager.common.validation.OnTemp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadByUrlRequest {

    @NotNull(message = "file.name.notnull")
    private String name;

    @NotNull(groups = OnCreate.class, message = "file.folderId.notnull")
    @Null(groups = OnTemp.class, message = "file.folderId.notnull")
    private Long folderId;

    private Long storeId;

    @Null(message = "file.scope.null")
    private ScopeType scope;

    @Null(message = "file.managedBy.null")
    private ManagedByType managedBy;
}