package store.emall.backend.security;

import store.emall.backend.security.dto.ForgotPasswordRequest;
import store.emall.backend.security.dto.ResetPasswordRequest;
import store.emall.backend.security.dto.SignupRequest;
import store.emall.backend.accounts.user.UserDto;

public interface AuthService {

    /**
     * Signup a new customer account.
     * Returns the created user DTO.
     */
    UserDto signup(SignupRequest request);

    /**
     * Validate refresh token and generate new access + refresh tokens.
     * Returns String[2]: [0] = accessToken, [1] = refreshToken
     */
    String[] refreshToken(String refreshToken);

    String requestPasswordReset(ForgotPasswordRequest request);
    void resetPassword(String resetToken, ResetPasswordRequest request);
    String resendPasswordResetOtp(String resetToken);
}
