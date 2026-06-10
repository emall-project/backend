package store.emall.backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "{auth.reset.otp.notblank}")
    private String otp;

    @NotBlank(message = "{auth.reset.password.notblank}")
    @Size(min = 8, message = "{auth.reset.password.size}")
    private String newPassword;

    @NotBlank(message = "{auth.reset.password.confirm.notblank}")
    private String confirmPassword;
}