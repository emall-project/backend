package store.emall.backend.security.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.emall.backend.common.phone_number.PhoneNumberDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneLoginRequest {

    @NotNull(message = "auth.phone.notnull")
    @Valid
    private PhoneNumberDto phone;
}
