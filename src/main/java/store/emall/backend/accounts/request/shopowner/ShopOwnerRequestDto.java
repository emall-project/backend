package store.emall.backend.accounts.request.shopowner;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.phone_number.PhoneNumberDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.accounts.request.shop.ShopRequestDto;
import store.emall.backend.accounts.user.Gender;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOwnerRequestDto {

    @Null(groups = OnCreate.class, message = "shopOwnerRequest.id.null")
    @NotNull(groups = OnUpdate.class, message = "shopOwnerRequest.id.notnull")
    @Positive(message = "shopOwnerRequest.id.positive")
    private Long id;

    @NotBlank(groups = OnCreate.class, message = "shopOwnerRequest.fullName.notblank")
    @Size(min = 2, max = 150, message = "shopOwnerRequest.fullName.size")
    private String fullName;

    @NotBlank(groups = OnCreate.class, message = "shopOwnerRequest.username.notblank")
    @Size(min = 3, max = 150, message = "shopOwnerRequest.username.size")
    private String username;

    @Email(message = "shopOwnerRequest.email.invalid")
    private String email;

    @NotNull(groups = OnCreate.class, message = "shopOwnerRequest.phone.notnull")
    @Valid
    private PhoneNumberDto phone;

    @NotNull(groups = OnCreate.class, message = "shopOwnerRequest.password.notnull")
    @Size(min = 8, message = "shopOwnerRequest.password.size")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#_+=\\-])[A-Za-z\\d@$!%*?&^#_+=\\-]{8,}$",
            message = "shopOwnerRequest.password.pattern"
    )
    private String password;

    @NotNull(groups = OnCreate.class, message = "user.gender.notnull")
    private Gender gender;

    @NotNull(groups = OnCreate.class, message = "user.age.notnull")
    @Min(value = 0, message = "shopOwnerRequest.age.min")
    @Max(value = 150, message = "shopOwnerRequest.age.max")
    private Integer age;

    @Size(min = 9, max = 20, message = "shopOwnerRequest.nationalIdNumber.size")
    private String nationalIdNumber;

    private UUID profilePictureUuid;
    private FileDto profilePictureImage;

    private ShopOwnerRequestStatus status;
    private String rejectionReason;
    private Long createdUserId;

    // Nested shop request
//    @NotNull(groups = OnCreate.class, message = "shopOwnerRequest.shopRequest.notnull")
    @Valid
    private ShopRequestDto shopRequest;
}