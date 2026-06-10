package store.emall.backend.accounts.user.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.phone_number.PhoneNumberDto;
import store.emall.backend.accounts.user.Gender;

import java.util.UUID;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 150, message = "user.fullName.size")
    private String fullName;

    @Email(message = "user.email.invalid")
    private String email;

    @Valid
    private PhoneNumberDto phone;

    private Gender gender;

    @Min(value = 0, message = "user.age.min")
    @Max(value = 150, message = "user.age.max")
    private Integer age;

    @Size(min = 9, max = 20, message = "user.nationalIdNumber.size")
    private String nationalIdNumber;

    private UUID profilePictureUuid;
    private FileDto profilePicture;
}