package store.emall.backend.accounts.user.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.accounts.user.UserDto;

@RestController
@RequestMapping("/api/users/{userId}/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/info")
    @PreAuthorize("(hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_SHOP_OWNER') or hasAuthority('ROLE_ADMIN')) and @auth.isOwner(#userId)")
    public EMallsResponseEntity<UserDto> getProfile(@PathVariable Long userId) {
        return EMallsResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping("/password")
    @PreAuthorize("(hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_SHOP_OWNER') or hasAuthority('ROLE_ADMIN')) and @auth.isOwner(#userId)")
    public EMallsResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @RequestBody @Valid ChangePasswordRequest request) {
        profileService.changePassword(userId, request);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/info")
    @PreAuthorize("(hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_SHOP_OWNER') or hasAuthority('ROLE_ADMIN')) and @auth.isOwner(#userId)")
    public EMallsResponseEntity<UserDto> updateProfile(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateProfileRequest request) {
        UserDto updated = profileService.updateProfile(userId, request);
        return EMallsResponseEntity.ok(updated);
    }
}
