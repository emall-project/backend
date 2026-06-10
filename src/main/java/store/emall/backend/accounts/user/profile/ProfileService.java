package store.emall.backend.accounts.user.profile;

import store.emall.backend.accounts.user.UserDto;

public interface ProfileService {

    UserDto getProfile(Long userId);
    void changePassword(Long userId, ChangePasswordRequest request);
    UserDto updateProfile(Long userId, UpdateProfileRequest request);
}
