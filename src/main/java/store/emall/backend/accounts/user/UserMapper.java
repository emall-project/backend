package store.emall.backend.accounts.user;


import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.accounts.user.profile.UpdateProfileRequest;
import store.emall.backend.accounts.user.role.Role;
import store.emall.backend.accounts.user.role.RoleMapper;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class UserMapper {

    private UserMapper() {}

    public static UserDto toFullDto(User user, FileDto profilePictureImage) {
        return Optional.ofNullable(user)
                .map(u -> UserDto.builder()
                        .userId(u.getUserId())
                        .username(u.getUsername())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phone(PhoneNumberMapper.fromPhoneString(u.getPhoneNumber()))
                        .isActive(u.getIsActive())
                        .role(RoleMapper.toDto(u.getRole()))
                        .gender(u.getGender())
                        .age(u.getAge())
                        .nationalIdNumber(u.getNationalIdNumber())
                        .profilePictureUuid(u.getProfilePictureUuid())
                        .profilePictureImage(profilePictureImage)
                        .isProtected(u.getIsProtected())
                        .build())
                .orElse(null);
    }

    public static UserDto toDto(User user) {
        return toFullDto(user, null);
    }

    public static User toEntity(UserDto dto, Role role) {
        return Optional.ofNullable(dto)
                .map(d -> User.builder()
                        .userId(d.getUserId())
                        .username(d.getUsername())
                        .fullName(d.getFullName())
                        .email(d.getEmail())
                        .phoneNumber(PhoneNumberMapper.toPhoneString(d.getPhone()))
                        .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                        .role(role)
                        .gender(d.getGender())
                        .age(d.getAge())
                        .nationalIdNumber(d.getNationalIdNumber())
                        .profilePictureUuid(d.getProfilePictureUuid())
                        .build())
                .orElse(null);
    }

    public static User merge(User existing, UserDto dto, Role role) {
        if(existing == null || dto == null) {
            return existing;
        }

        if(role != null) {
            existing.setRole(role);
        }

        if(dto.getPhone() != null) {
            existing.setPhoneNumber(PhoneNumberMapper.toPhoneString(dto.getPhone()));
        }

        existing.setFullName(firstNonNull(dto.getFullName(), existing.getFullName()));
        existing.setEmail(firstNonNull(dto.getEmail(), existing.getEmail()));
        existing.setIsActive(firstNonNull(dto.getIsActive(), existing.getIsActive()));
        existing.setAge(firstNonNull(dto.getAge(), existing.getAge()));
        existing.setGender(firstNonNull(dto.getGender(), existing.getGender()));
        existing.setNationalIdNumber(firstNonNull(dto.getNationalIdNumber(), existing.getNationalIdNumber()));
        existing.setProfilePictureUuid(firstNonNull(dto.getProfilePictureUuid(), existing.getProfilePictureUuid()));
        return existing;
    }

    public static UserBasicDto toBasicUser(User user) {
        return Optional.ofNullable(user)
                .map(u -> UserBasicDto.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .build())
                .orElse(null);
    }

    public static UserInfoDto toInfoDto(User entity) {
        if (entity == null) return null;

        return UserInfoDto.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .isActive(entity.getIsActive())
                .role(entity.getRole().getCode())
                .build();
    }

    public static User mergeProfile(User existing, UpdateProfileRequest request) {
        if (existing == null || request == null) {
            return existing;
        }

        existing.setFullName(firstNonNull(request.getFullName(), existing.getFullName()));
        existing.setEmail(firstNonNull(request.getEmail(), existing.getEmail()));
        if (request.getPhone() != null) {
            existing.setPhoneNumber(PhoneNumberMapper.toPhoneString(request.getPhone()));
        }
        existing.setGender(firstNonNull(request.getGender(), existing.getGender()));
        existing.setAge(firstNonNull(request.getAge(), existing.getAge()));
        existing.setNationalIdNumber(firstNonNull(request.getNationalIdNumber(), existing.getNationalIdNumber()));
        existing.setProfilePictureUuid(firstNonNull(request.getProfilePictureUuid(), existing.getProfilePictureUuid()));

        return existing;
    }
}
