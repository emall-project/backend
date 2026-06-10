package store.emall.backend.accounts.user;

import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<UserDto>> getAll(Pageable pageable, UserSpec spec) {
        PaginatedResponse<UserDto> users = userService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<UserDto>> getUsers(UserSpec spec) {
        List<UserDto> users = userService.getAllUserList(spec);
        return EMallsResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<UserDto> getById(@PathVariable @Positive Long id) {
        UserDto user = userService.getById(id);
        return EMallsResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/info")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<UserInfoDto> getUserInfo(@PathVariable Long userId) {
        return EMallsResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PostMapping
    public EMallsResponseEntity<UserDto> create(@RequestBody @Validated({Default.class, OnCreate.class}) UserDto user) {
        UserDto dto = userService.create(user);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    public EMallsResponseEntity<UserDto> update(@RequestBody @Validated({Default.class, OnUpdate.class}) UserDto user) {
        UserDto dto = userService.update(user);
        return EMallsResponseEntity.ok(dto);
    }

    @PutMapping("/deactivate/{id}")
    public EMallsResponseEntity<Void> deactivate(@PathVariable @Positive Long id) {
        userService.deactivate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/activate/{id}")
    public EMallsResponseEntity<Void> activate(@PathVariable @Positive Long id) {
        userService.activate(id);
        return EMallsResponseEntity.noContent(null);
    }
}
