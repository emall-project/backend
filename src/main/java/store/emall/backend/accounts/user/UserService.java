package store.emall.backend.accounts.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface UserService {
    PaginatedResponse<UserDto> getAll(Pageable pageable, Specification<User> spec);
    List<UserDto> getAllUserList(Specification<User> spec);
    UserDto getById(Long id);
    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto);
    void deactivate(Long id);
    void activate(Long id);

    UserInfoDto getUserInfo(Long userId);
}
