package store.emall.backend.accounts.user.role;

import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<RoleDto>> getAll(Pageable pageable, RoleSpec spec) {
        PaginatedResponse<RoleDto> roleDtos = roleService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(roleDtos);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<RoleDto>> getRoles(RoleSpec spec) {
        List<RoleDto> roles = roleService.getAllRoleList(spec);
        return EMallsResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<RoleDto> getById(@PathVariable @Positive Long id) {
        RoleDto roleDto = roleService.getById(id);
        return EMallsResponseEntity.ok(roleDto);
    }

    @PostMapping
    public EMallsResponseEntity<RoleDto> create(@RequestBody @Validated({Default.class, OnCreate.class}) RoleDto roleDto) {
        RoleDto dto = roleService.create(roleDto);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    public EMallsResponseEntity<RoleDto> update(@RequestBody @Validated({Default.class, OnUpdate.class}) RoleDto roleDto) {
        RoleDto dto = roleService.update(roleDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity delete(@PathVariable @Positive Long id) {
        roleService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
