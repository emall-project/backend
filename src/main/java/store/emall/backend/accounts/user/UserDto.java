package store.emall.backend.accounts.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.phone_number.PhoneNumberDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.accounts.user.role.RoleDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#_+=\\-])[A-Za-z\\d@$!%*?&^#_+=\\-]{8,}$";

    @Null(groups = OnCreate.class, message = "user.userId.null")
    @NotNull(groups = OnUpdate.class, message = "user.userId.notnull")
    @Positive(message = "user.userId.positive")
    private Long userId;

    @NotBlank(groups = OnCreate.class, message = "user.username.notblank")
    @Null(groups = OnUpdate.class, message = "user.username.null")
    @Size(min = 3, max = 150, message = "user.username.size")
    private String username;

    private String fullName;

    @Email(message = "user.email.invalid")
    private String email;

    @NotNull(groups = OnCreate.class, message = "user.phone.notnull")
    @Valid
    private PhoneNumberDto phone;

    private RoleDto role;
    private Boolean isActive;

    @NotNull(groups = OnCreate.class, message = "user.password.notblank")
    @Null(groups = OnUpdate.class, message = "user.password.null")
    @Size(min = 8, message = "user.password.size")
    @Pattern(regexp = PASSWORD_REGEX, message = "user.password.pattern")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(groups = OnCreate.class, message = "user.gender.notnull")
    private Gender gender;

    @NotNull(groups = OnCreate.class, message = "user.age.notnull")
    @Min(value = 0, message = "user.age.min")
    @Max(value = 150, message = "user.age.max")
    private Integer age;

    @Size(min = 9, max = 20, message = "user.nationalIdNumber.size")
    private String nationalIdNumber;

    private UUID profilePictureUuid;
    private FileDto profilePictureImage;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isProtected;

}
