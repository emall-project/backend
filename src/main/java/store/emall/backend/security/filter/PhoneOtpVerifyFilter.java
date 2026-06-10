package store.emall.backend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.dto.OtpVerifyRequest;
import store.emall.backend.security.jwt.JwtService;
import store.emall.backend.security.otp.OtpService;
import store.emall.backend.security.provider.PhoneAuthenticationToken;

import java.io.IOException;

/**
 * Intercepts POST /api/auth/phone/verify-otp.
 * Reads { tempToken, otp }, validates the OTP, then delegates
 * to PhoneAuthenticationProvider which loads the user by phone.
 * On success, PhoneAuthSuccessHandler generates tokens.
 * On failure, PhoneAuthFailureHandler returns error.
 */
public class PhoneOtpVerifyFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final OtpService otpService;

    public PhoneOtpVerifyFilter(
            String verifyUrl,
            AuthenticationManager authenticationManager,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            ObjectMapper objectMapper,
            JwtService jwtService,
            OtpService otpService) {

        super(verifyUrl);
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        this.otpService = otpService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        OtpVerifyRequest otpRequest = objectMapper.readValue(request.getInputStream(), OtpVerifyRequest.class);

        String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if(!StringUtils.hasText(authHeader) || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)){
            throw new OtpAuthenticationException("Missing or invalid Authorization header.");
        }
        String tempToken = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());

        // Validate temp token
        if (!jwtService.isTokenValid(tempToken) || !jwtService.isTempToken(tempToken)) {
            throw new OtpAuthenticationException("Invalid or expired temporary token");
        }

        // Extract phone from temp token
        String phoneNumber = jwtService.extractPhone(tempToken);
        if (phoneNumber == null) {
            throw new OtpAuthenticationException("Invalid temporary token: missing phone");
        }

        // Verify OTP
        if (otpService.isOtpExpired(phoneNumber)) {
            throw new OtpAuthenticationException("OTP has expired. Please request a new one");
        }

        if (!otpService.verifyOtp(phoneNumber, otpRequest.getOtp())) {
            throw new OtpAuthenticationException("Invalid OTP code");
        }

        // OTP verified -- delegate to PhoneAuthenticationProvider to load user
        PhoneAuthenticationToken authToken = new PhoneAuthenticationToken(phoneNumber);
        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }

    /**
     * Custom exception for OTP-specific failures so the failure handler can distinguish them.
     */
    public static class OtpAuthenticationException extends AuthenticationException {
        public OtpAuthenticationException(String msg) {
            super(msg);
        }
    }
}
