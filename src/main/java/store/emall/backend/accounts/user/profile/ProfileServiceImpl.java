package store.emall.backend.accounts.user.profile;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.accounts.user.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;


    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        User user = findActiveCustomer(userId);
        FileDto profilePicture = fileService.getById(user.getProfilePictureUuid());
        return UserMapper.toFullDto(user, profilePicture);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findActiveCustomer(userId);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw UserExceptions.invalidCurrentPassword();
        }

        // Prevent reusing the same password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw UserExceptions.newPasswordSameAsCurrent();
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findActiveCustomer(userId);

        // Uniqueness checks for fields that might be changed
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw UserExceptions.emailExists();
        }

        if (request.getPhone() != null) {
            String newPhone = PhoneNumberMapper.toPhoneString(request.getPhone());
            if (!newPhone.equals(user.getPhoneNumber()) && userRepository.existsByPhoneNumber(newPhone)) {
                throw UserExceptions.phoneExists();
            }
        }

        if (request.getNationalIdNumber() != null && !request.getNationalIdNumber().equals(user.getNationalIdNumber())
                && userRepository.existsByNationalIdNumber(request.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        // Validate new profile picture if provided and different
        if (request.getProfilePictureUuid() != null
                && !request.getProfilePictureUuid().equals(user.getProfilePictureUuid())) {
            fileService.getAndValidateImage(request.getProfilePictureUuid(), "ProfilePictureUuid");
        }

        // Merge changes using the dedicated mapper method
        UserMapper.mergeProfile(user, request);

        User saved = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);

        FileDto profilePicture = fileService.getById(saved.getProfilePictureUuid());
        return UserMapper.toFullDto(user, profilePicture);
    }

    private User findActiveCustomer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserExceptions::userNotFound);

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw UserExceptions.userNotFound();
        }

        String roleCode = user.getRole().getCode();
        if (!"ROLE_CUSTOMER".equals(roleCode) && !"ROLE_SHOP_OWNER".equals(roleCode) && !"ROLE_ADMIN".equals(roleCode)) {
            throw UserExceptions.unauthorizedProfileUpdate();
        }

        return user;
    }

}
