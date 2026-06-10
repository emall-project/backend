package store.emall.backend.security;

import org.springframework.http.HttpStatus;
import store.emall.backend.accounts.common.exception.EMallsException;
import store.emall.backend.accounts.common.message.MessageKey;
import store.emall.backend.accounts.common.response.ErrorCode;

import java.util.List;

public final class AuthExceptions {

    private AuthExceptions() {}

    public static EMallsException invalidCredentials() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_INVALID_CREDENTIALS.getKey())
                .build();
    }

    public static EMallsException accountDisabled() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.AUTH_ACCOUNT_DISABLED.getKey())
                .build();
    }

    public static EMallsException invalidToken() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_INVALID_TOKEN.getKey())
                .build();
    }

    public static EMallsException tokenExpired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_TOKEN_EXPIRED.getKey())
                .build();
    }

    public static EMallsException invalidOtp() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_INVALID_OTP.getKey())
                .build();
    }

    public static EMallsException otpExpired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_OTP_EXPIRED.getKey())
                .build();
    }

    public static EMallsException phoneNotRegistered() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AUTH_PHONE_NOT_REGISTERED.getKey())
                .build();
    }

    public static EMallsException accessDenied() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.AUTH_ACCESS_DENIED.getKey())
                .build();
    }

    public static EMallsException accessDenied(String message) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(message)
                .build();
    }

    public static EMallsException forgotPasswordUserNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AUTH_FORGOT_USER_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("username", MessageKey.AUTH_FORGOT_USER_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException passwordMismatch() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AUTH_RESET_PASSWORD_MISMATCH.getKey())
                .errorCode(List.of(new ErrorCode("confirmPassword", MessageKey.AUTH_RESET_PASSWORD_MISMATCH.getKey())))
                .build();
    }

    public static EMallsException newPasswordSameAsCurrent() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AUTH_RESET_PASSWORD_SAME_AS_CURRENT.getKey())
                .errorCode(List.of(new ErrorCode("newPassword", MessageKey.AUTH_RESET_PASSWORD_SAME_AS_CURRENT.getKey())))
                .build();
    }

    public static EMallsException invalidResetToken() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_RESET_TOKEN_INVALID.getKey())
                .build();
    }

    public static EMallsException noEmailRegistered() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.AUTH_FORGOT_EMAIL_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("email", MessageKey.AUTH_FORGOT_EMAIL_NOT_FOUND.getKey())))

                .build();
    }
}
