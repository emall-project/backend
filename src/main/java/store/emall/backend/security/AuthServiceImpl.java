package store.emall.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.email.EmailService;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.mediamanager.file.service.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.security.dto.ForgotPasswordRequest;
import store.emall.backend.security.dto.ResetPasswordRequest;
import store.emall.backend.security.dto.SignupRequest;
import store.emall.backend.security.jwt.JwtService;
import store.emall.backend.security.otp.OtpService;
import store.emall.backend.security.otp.OtpVerifyResult;
import store.emall.backend.security.userdetails.CustomUserDetails;
import store.emall.backend.security.userdetails.CustomUserDetailsService;
import store.emall.backend.accounts.user.*;
import store.emall.backend.accounts.user.role.Role;
import store.emall.backend.accounts.user.role.RoleExceptions;
import store.emall.backend.accounts.user.role.RoleRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;
    private final OtpService otpService;


    // ==================== Signup ====================

    @Override
    @Transactional
    public UserDto signup(SignupRequest request) {
        // Check for duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            throw UserExceptions.usernameExists();
        }

        String phoneNumber = PhoneNumberMapper.toPhoneString(request.getPhone());
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw UserExceptions.phoneExists();
        }

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw UserExceptions.emailExists();
        }

        if (request.getNationalIdNumber() != null
                && userRepository.existsByNationalIdNumber(request.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        if (request.getProfilePictureUuid() != null) {
            getAndValidateImage(request.getProfilePictureUuid());
        }

        // Find CUSTOMER role
        Role customerRole = roleRepository.findByCode(SecurityConstants.ROLE_CUSTOMER)
                .orElseThrow(RoleExceptions::roleNotFound);

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .age(request.getAge())
                .nationalIdNumber(request.getNationalIdNumber())
                .profilePictureUuid(request.getProfilePictureUuid())
                .role(customerRole)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("New customer account created: {}", saved.getUsername());

        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional
    public String[] refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw AuthExceptions.invalidToken();
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserExceptions::userNotFound);

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw AuthExceptions.accountDisabled();
        }

        // Re-load UserDetails fresh from DB (picks up any role/shop/profile changes)
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        // Build role-specific access token
        String newAccessToken;
        if (SecurityConstants.ROLE_SHOP_OWNER.equals(userDetails.getRoleCode())) {
            newAccessToken = jwtService.generateAccessToken(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getFullName(),
                    userDetails.getRoleCode(),
                    userDetails.getShopIds()
            );
        } else if (SecurityConstants.ROLE_CUSTOMER.equals(userDetails.getRoleCode())) {
            newAccessToken = jwtService.generateAccessToken(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getFullName(),
                    userDetails.getRoleCode(),
                    userDetails.getAge(),
                    userDetails.getGender()
            );
        } else {
            newAccessToken = jwtService.generateAccessToken(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getFullName(),
                    userDetails.getRoleCode()
            );
        }

        String newRefreshToken = jwtService.generateRefreshToken(
                user.getUserId(),
                user.getUsername()
        );

        log.info("Tokens refreshed for user: {} (role={})", username, userDetails.getRoleCode());
        return new String[]{newAccessToken, newRefreshToken};
    }

    @Override
    public String requestPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(AuthExceptions::forgotPasswordUserNotFound);

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw AuthExceptions.accountDisabled();
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw AuthExceptions.noEmailRegistered();
        }

        String otp = otpService.generateOtp(user.getUsername());

        emailService.sendOtpEmail(user.getEmail(), user.getUsername(), otp);

        log.info("Password reset OTP sent for user '{}'", user.getUsername());
        return jwtService.generateResetToken(user.getUsername());
    }


    @Override
    @Transactional
    public void resetPassword(String resetToken, ResetPasswordRequest request) {
        if (!jwtService.isTokenValid(resetToken) || !jwtService.isResetToken(resetToken)) {
            throw AuthExceptions.invalidResetToken();
        }

        String username = jwtService.extractUsername(resetToken);
        if (username == null) {
            throw AuthExceptions.invalidResetToken();
        }

        OtpVerifyResult otpResult = otpService.verifyOtpWithReason(username, request.getOtp());
        if (otpResult == OtpVerifyResult.EXPIRED) {
            throw AuthExceptions.otpExpired();
        }
        if (otpResult == OtpVerifyResult.INVALID) {
            throw AuthExceptions.invalidOtp();
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw AuthExceptions.passwordMismatch();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserExceptions::userNotFound);

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw AuthExceptions.newPasswordSameAsCurrent();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password successfully reset for user '{}'", username);
    }

    @Override
    public String resendPasswordResetOtp(String resetToken) {
        if (!jwtService.isTokenValid(resetToken) || !jwtService.isResetToken(resetToken)) {
            throw AuthExceptions.invalidResetToken();
        }

        String username = jwtService.extractUsername(resetToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserExceptions::userNotFound);

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw AuthExceptions.accountDisabled();
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw AuthExceptions.noEmailRegistered();
        }

        String otp = otpService.generateOtp(username);

        emailService.sendOtpEmail(user.getEmail(), username, otp);

        log.info("OTP resent for user '{}'", username);
        return jwtService.generateResetToken(username);
    }


    private void getAndValidateImage(UUID uuid) {
        FileDto fileDto = fileService.getById(uuid);
        if (!isImage(fileDto.getMimeType())) {
            throw UserExceptions.invalidFileType();
        }
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

}
