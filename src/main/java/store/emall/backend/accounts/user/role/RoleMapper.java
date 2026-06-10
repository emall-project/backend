package store.emall.backend.accounts.user.role;

import java.util.Optional;

public class RoleMapper {

    public static RoleDto toDto(Role role) {
        return Optional.ofNullable(role)
                .map(tmp -> RoleDto.builder()
                        .roleId(role.getRoleId())
                        .code(role.getCode())
                        .name(role.getName())
                        .build()).orElse(new RoleDto());
    }

    public static Role toEntity(RoleDto roleDto) {
        return Optional.ofNullable(roleDto)
                .map(tmp -> Role.builder()
                        .roleId(roleDto.getRoleId())
                        .code(roleDto.getCode())
                        .name(roleDto.getName())
                        .build()).orElse(null);
    }
}
