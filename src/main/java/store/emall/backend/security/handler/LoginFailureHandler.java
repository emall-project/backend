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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Handles failed username/password login attempts.
 * Maps specific exceptions to meaningful error responses.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.warn("Login failed for request to {}: {}", request.getRequestURI(), exception.getMessage());

        HttpStatus status;
        String message;

        if (exception instanceof DisabledException) {
            status = HttpStatus.FORBIDDEN;
            message = "Your account has been disabled. Please contact support";
        } else if (exception instanceof BadCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Invalid username or password";
        } else {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authentication failed";
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
