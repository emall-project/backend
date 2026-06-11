package store.emall.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.security.dto.ForgotPasswordRequest;
import store.emall.backend.security.dto.ResetPasswordRequest;
import store.emall.backend.security.dto.SignupRequest;
import store.emall.backend.security.dto.AuthTokenResponse;
import store.emall.backend.accounts.user.UserDto;

/**
 * Auth endpoints that are NOT handled by security filters.
 *
 * Login endpoints are handled by filters:
 *   POST /api/auth/login              -> UsernamePasswordLoginFilter
 *   POST /api/auth/phone/request-otp  -> PhoneOtpRequestFilter
 *   POST /api/auth/phone/verify-otp   -> PhoneOtpVerifyFilter
 *
 * This controller handles:
 *   POST /api/auth/signup             -> Create new customer account
 *   POST /api/auth/refresh-token      -> Refresh access token (reads X-Refresh-Token header)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/signup
     * Register a new customer account.
     * Returns the created UserDto (FE should redirect to login page).
     */
    @PostMapping("/signup")
    public EMallsResponseEntity<UserDto> signup(@RequestBody @Valid SignupRequest request) {
        UserDto user = authService.signup(request);
        return EMallsResponseEntity.created(user);
    }

    /**
     * POST /api/auth/refresh-token
     * FE sends the refresh token in the X-Refresh-Token header.
     * BE validates it and returns new accessToken + refreshToken in response headers and body.
     * No request body needed.
     */
    @PostMapping("/refresh-token")
    public EMallsResponseEntity<AuthTokenResponse> refreshToken(
            @RequestHeader(SecurityConstants.REFRESH_TOKEN_HEADER) String refreshToken,
            HttpServletResponse response) {

        String[] tokens = authService.refreshToken(refreshToken);

        response.setHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.TOKEN_PREFIX + tokens[0]);
        response.setHeader(SecurityConstants.REFRESH_TOKEN_HEADER, tokens[1]);

        return EMallsResponseEntity.ok(AuthTokenResponse.bearer(tokens[0], tokens[1]));
    }

    /**
     * POST /api/auth/forgot-password/request
     * Body: { "email": "user@example.com" }
     * Response: 200 OK + X-Temp-Token header (RESET token, valid 10 min)
     * The OTP is sent to the user's email.
     */
    @PostMapping("/forgot-password/request")
    public EMallsResponseEntity<Void> requestPasswordReset(
            @RequestBody @Valid ForgotPasswordRequest request,
            HttpServletResponse response) {

        String resetToken = authService.requestPasswordReset(request);
        response.setHeader(SecurityConstants.TEMP_TOKEN_HEADER, resetToken);
        return EMallsResponseEntity.ok(null);
    }

    /**
     * POST /api/auth/forgot-password/reset
     * Header: Authorization: Bearer <reset-token>
     * Body: { "otp": "123456", "newPassword": "...", "confirmPassword": "..." }
     * Response: 200 OK — password updated.
     */
    @PostMapping("/forgot-password/reset")
    public EMallsResponseEntity<Void> resetPassword(
            @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String authHeader,
            @RequestBody @Valid ResetPasswordRequest request) {

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw AuthExceptions.invalidResetToken();
        }
        String resetToken = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
        authService.resetPassword(resetToken, request);
        return EMallsResponseEntity.ok(null);
    }

    /**
     * POST /api/auth/forgot-password/resend
     * Header: Authorization: Bearer <current-reset-token>
     * Body: { "deliveryEmail": "user@example.com" }
     * Response: 200 OK + new X-Temp-Token header
     */
    @PostMapping("/forgot-password/resend")
    public EMallsResponseEntity<Void> resendOtp(
            @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String authHeader,
            HttpServletResponse response) {

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw AuthExceptions.invalidResetToken();
        }
        String resetToken = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
        String newResetToken = authService.resendPasswordResetOtp(resetToken);
        response.setHeader(SecurityConstants.TEMP_TOKEN_HEADER, newResetToken);
        return EMallsResponseEntity.ok(null);
    }
}
