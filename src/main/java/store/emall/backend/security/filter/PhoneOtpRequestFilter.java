package store.emall.backend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.dto.PhoneLoginRequest;
import store.emall.backend.security.jwt.JwtService;
import store.emall.backend.security.otp.OtpService;
import store.emall.backend.accounts.user.User;
import store.emall.backend.accounts.user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Intercepts POST /api/auth/phone/request-otp.
 * Reads the phone number, checks user exists and is active,
 * generates OTP, and returns a temporary token.
 *
 * This is NOT an authentication filter -- it's the first step
 * of a 2-step flow. Authentication happens in PhoneOtpVerifyFilter.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PhoneOtpRequestFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Only intercept POST /api/auth/phone/request-otp
        if (!isOtpRequestPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            PhoneLoginRequest phoneRequest = objectMapper.readValue(request.getInputStream(), PhoneLoginRequest.class);
            String phoneNumber = PhoneNumberMapper.toPhoneString(phoneRequest.getPhone());

            // Check user exists
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                writeError(response, HttpStatus.NOT_FOUND, "No account found with this phone number");
                return;
            }

            User user = userOpt.get();
            if (Boolean.FALSE.equals(user.getIsActive())) {
                writeError(response, HttpStatus.FORBIDDEN, "Your account has been disabled");
                return;
            }

            // Generate OTP and temp token
            otpService.generateOtp(phoneNumber);
            String tempToken = jwtService.generateTempToken(phoneNumber);

            // Put temp token in header -- FE reads it from X-Temp-Token header
            response.setHeader(SecurityConstants.TEMP_TOKEN_HEADER, tempToken);
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("Error processing phone OTP request: {}", e.getMessage());
            writeError(response, HttpStatus.BAD_REQUEST, "Invalid phone OTP request");
        }
    }

    private boolean isOtpRequestPath(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/api/auth/phone/request-otp".equals(request.getServletPath());
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
