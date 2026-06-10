package store.emall.backend.accounts.user.profile;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import store.emall.backend.accounts.user.UserDto;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "user.password.notblank")
    private String currentPassword;

    @NotBlank(message = "user.password.notblank")
    @Size(min = 8, message = "user.password.size")
    @Pattern(regexp = UserDto.PASSWORD_REGEX, message = "user.password.pattern")
    private String newPassword;

    @NotBlank(message = "user.password.notblank")
    private String confirmNewPassword;

    @AssertTrue(message = "user.password.confirm.mismatch")
    public boolean isPasswordConfirmed() {
        if (newPassword == null || confirmNewPassword == null) {
            return true;
        }
        return newPassword.equals(confirmNewPassword);
    }
}