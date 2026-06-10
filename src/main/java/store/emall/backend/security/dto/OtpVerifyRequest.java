package store.emall.backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpVerifyRequest {

    @NotBlank(message = "auth.otp.notblank")
    @Size(min = 6, max = 6, message = "auth.otp.size")
    private String otp;
}
