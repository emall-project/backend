package store.emall.backend.accounts.user.role;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    @Null(groups = OnCreate.class, message = "role.roleId.null")
    @NotNull(groups = OnUpdate.class, message = "role.roleId.notnull")
    @Positive(message = "role.roleId.positive")
    private Long roleId;

    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100)
    private String code;

    @Size(min = 1, max = 100)
    private String name;
}
