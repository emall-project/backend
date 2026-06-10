package store.emall.backend.accounts.user.role;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.accounts.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RoleDto> getAll(Pageable pageable, Specification<Role> spec) {
        Page<RoleDto> rolePage = roleRepository.findAll(spec, pageable)
                .map(RoleMapper::toDto);

        return PaginatedResponse.of(rolePage);

    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoleList(Specification<Role> spec) {

        List<Role> roles = (spec == null)
                ? roleRepository.findAll()
                : roleRepository.findAll(spec);

        return roles.stream()
                .map(RoleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(RoleExceptions::roleNotFound);

        return RoleMapper.toDto(role);
    }

    @Override
    public RoleDto create(RoleDto roleDto) {
        if(roleRepository.existsByCode(roleDto.getCode())) {
            throw RoleExceptions.roleExists();
        }

        Role role = RoleMapper.toEntity(roleDto);
        Role saved = roleRepository.save(role);

        return RoleMapper.toDto(saved);
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        Role existing = roleRepository.findById(roleDto.getRoleId())
                .orElseThrow(RoleExceptions::roleNotFound);

        if (roleDto.getCode() != null && !roleDto.getCode().equals(existing.getCode())
                && roleRepository.existsByCode(roleDto.getCode())) {
            throw RoleExceptions.roleExists();
        }

        existing.setCode(firstNonNull(roleDto.getCode(), existing.getCode()));
        existing.setName(firstNonNull(roleDto.getName(), existing.getName()));
        Role updated = roleRepository.save(existing);
        return RoleMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(RoleExceptions::roleNotFound);

        if (userRepository.existsByRole(role)) {
            throw RoleExceptions.roleHasUsers();
        }

        roleRepository.delete(role);
    }
}
