package store.emall.backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "auth.username.notblank")
    private String username;

    @NotBlank(message = "auth.password.notblank")
    private String password;
}
