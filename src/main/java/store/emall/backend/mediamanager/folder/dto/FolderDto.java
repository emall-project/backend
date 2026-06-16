package store.emall.backend.mediamanager.folder.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderDto {

    @Null(groups = OnCreate.class, message = "folder.id.null")
    @NotNull(groups = OnUpdate.class, message = "folder.id.notnull")
    @Positive(message = "folder.id.positive")
    private Long id;

    @NotBlank(message = "folder.name.notblank")
    @Size(max = 50, message = "folder.name.size")
    private String name;

    @Null(groups = OnUpdate.class, message = "folder.shopId.null")
    private Long shopId;

    @Positive(message = "folder.parentId.positive")
    private Long parentId;

    @Null(message = "folder.scope.null")
    private ScopeType scope;

    @Null(message = "folder.managedBy.null")
    private ManagedByType managedBy;
}
