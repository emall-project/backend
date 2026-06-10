package store.emall.backend.accounts.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.accounts.user.role.Role;
import store.emall.backend.accounts.user.role.RoleExceptions;
import store.emall.backend.accounts.user.role.RoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserDto> getAll(Pageable pageable, Specification<User> spec) {
        Page<UserDto> userPage = userRepository.findAll(spec, pageable)
                .map(this::toDtoWithMedia);

        return PaginatedResponse.of(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUserList(Specification<User> spec) {

        List<User> users = (spec == null)
                ? userRepository.findAll()
                : userRepository.findAll(spec);


        return users.stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserExceptions::userNotFound);

        return toDtoWithMedia(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserExceptions::userNotFound);

        // Inactive users are treated as not found for external services
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw UserExceptions.userNotFound();
        }

        log.debug("User info requested by internal service: userId={}", userId);
        return UserMapper.toInfoDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw UserExceptions.usernameExists();
        }

        if (userRepository.existsByPhoneNumber(PhoneNumberMapper.toPhoneString(userDto.getPhone()))) {
            throw UserExceptions.phoneExists();
        }

        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
            throw UserExceptions.emailExists();
        }

        if (userDto.getNationalIdNumber() != null && userRepository.existsByNationalIdNumber(userDto.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        Role role = roleRepository.findById(userDto.getRole().getRoleId())
                .orElseThrow(RoleExceptions::roleNotFound);

        // Validate profile picture if provided
        FileDto profilePictureImage = null;
        if (userDto.getProfilePictureUuid() != null) {
            profilePictureImage = getAndValidateImage(userDto.getProfilePictureUuid());
        }

        User user = UserMapper.toEntity(userDto, role);

        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User savedUser = userRepository.save(user);
        log.info("User created: userId={}, username={}", savedUser.getUserId(), savedUser.getUsername());

        return UserMapper.toFullDto(savedUser, profilePictureImage);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {
        User existing = userRepository.findById(userDto.getUserId())
                .orElseThrow(UserExceptions::userNotFound);

        if (Boolean.TRUE.equals(existing.getIsProtected())) {
            throw UserExceptions.userIsProtected();
        }

        if (userDto.getPhone() != null) {
            String phoneNumber = PhoneNumberMapper.toPhoneString(userDto.getPhone());
            if (!phoneNumber.equals(existing.getPhoneNumber()) && userRepository.existsByPhoneNumber(phoneNumber)) {
                throw UserExceptions.phoneExists();
            }
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existing.getEmail())
                && userRepository.existsByEmail(userDto.getEmail())) {
            throw UserExceptions.emailExists();
        }

        if (userDto.getNationalIdNumber() != null && !userDto.getNationalIdNumber().equals(existing.getNationalIdNumber())
                && userRepository.existsByNationalIdNumber(userDto.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        Role role = null;
        if (userDto.getRole() != null && userDto.getRole().getRoleId() != null) {
            role = roleRepository.findById(userDto.getRole().getRoleId())
                    .orElseThrow(RoleExceptions::roleNotFound);
        }

        // Validate new profile picture if provided and different
        if (userDto.getProfilePictureUuid() != null
                && !userDto.getProfilePictureUuid().equals(existing.getProfilePictureUuid())) {
            getAndValidateImage(userDto.getProfilePictureUuid());
        }

        UserMapper.merge(existing, userDto, role);
        User savedUser = userRepository.save(existing);

        log.info("User updated: userId={}", savedUser.getUserId());

        return toDtoWithMedia(savedUser);
    }

    @Override
    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserExceptions::userNotFound);

        if (Boolean.TRUE.equals(user.getIsProtected())) {
            throw UserExceptions.userIsProtected();
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            return;
        }
        user.setIsActive(Boolean.FALSE);
        userRepository.save(user);
        log.info("User deactivated: userId={}", id);
    }

    @Override
    public void activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserExceptions::userNotFound);

        if (Boolean.TRUE.equals(user.getIsProtected())) {
            throw UserExceptions.userIsProtected();
        }

        if (Boolean.TRUE.equals(user.getIsActive())) {
            return;
        }
        user.setIsActive(Boolean.TRUE);
        userRepository.save(user);
        log.info("User activated: userId={}", id);
    }

    @Transactional
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    userRepository.save(user);
                });
    }

    private UserDto toDtoWithMedia(User user) {
        FileDto profilePictureImage = fetchImageSafely(user.getProfilePictureUuid());
        return UserMapper.toFullDto(user, profilePictureImage);
    }

    private FileDto fetchImageSafely(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return fileService.getById(uuid);
    }

    private FileDto getAndValidateImage(UUID uuid) {
        FileDto fileDto = fileService.getById(uuid);
        if (!isImage(fileDto.getMimeType())) {
            throw UserExceptions.invalidFileType();
        }
        return fileDto;
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

}
