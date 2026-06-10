package store.emall.backend.security.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.phone_number.PhoneNumberDto;
import store.emall.backend.accounts.user.Gender;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {

    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#_+=\\-])[A-Za-z\\d@$!%*?&^#_+=\\-]{8,}$";

    @NotBlank(message = "auth.username.notblank")
    @Size(min = 3, max = 150, message = "user.username.size")
    private String username;

    @NotBlank(message = "auth.fullName.notblank")
    private String fullName;

    @Email(message = "user.email.invalid")
    private String email;

    @NotNull(message = "auth.phone.notnull")
    @Valid
    private PhoneNumberDto phone;

    @NotBlank(message = "auth.password.notblank")
    @Size(min = 8, message = "user.password.size")
    @Pattern(regexp = PASSWORD_REGEX, message = "user.password.pattern")
    private String password;

    @NotNull(message = "user.gender.notnull")
    private Gender gender;

    @NotNull(message = "user.age.notnull")
    @Min(value = 0, message = "user.age.min")
    @Max(value = 150, message = "user.age.max")
    private Integer age;

    @Size(min = 9, max = 20, message = "user.nationalIdNumber.size")
    private String nationalIdNumber;

    private UUID profilePictureUuid;
}
