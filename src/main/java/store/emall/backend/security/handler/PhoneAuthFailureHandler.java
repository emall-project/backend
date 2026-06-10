package store.emall.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import store.emall.backend.security.filter.PhoneOtpVerifyFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Handles failed phone OTP verification attempts.
 * Distinguishes between OTP-specific errors and general auth errors.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PhoneAuthFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.warn("Phone auth failed for request to {}: {}", request.getRequestURI(), exception.getMessage());

        HttpStatus status;
        String message;

        if (exception instanceof PhoneOtpVerifyFilter.OtpAuthenticationException) {
            // OTP-specific errors (invalid OTP, expired OTP, invalid temp token)
            status = HttpStatus.UNAUTHORIZED;
            message = exception.getMessage();
        } else if (exception instanceof DisabledException) {
            status = HttpStatus.FORBIDDEN;
            message = "Your account has been disabled. Please contact support";
        } else if (exception instanceof BadCredentialsException) {
            status = HttpStatus.NOT_FOUND;
            message = "No account found with this phone number";
        } else {
            status = HttpStatus.UNAUTHORIZED;
            message = "Phone authentication failed";
        }

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(), Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "path", request.getRequestURI(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
