package store.emall.backend.accounts.user.role;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface RoleService {

    RoleDto create(RoleDto roleDto);
    RoleDto getById(Long id);
    PaginatedResponse<RoleDto> getAll(Pageable pageable, Specification<Role> spec);
    List<RoleDto> getAllRoleList(Specification<Role> spec);
    RoleDto update(RoleDto roleDto);
    void delete(Long id);
}
