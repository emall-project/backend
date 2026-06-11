package store.emall.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.dto.AuthTokenResponse;
import store.emall.backend.security.jwt.JwtService;
import store.emall.backend.security.userdetails.CustomUserDetails;
import store.emall.backend.accounts.user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Update last login timestamp
        userRepository.findByUsername(userDetails.getUsername())
                .ifPresent(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    userRepository.save(user);
                });

        String accessToken = buildAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(
                userDetails.getUserId(),
                userDetails.getUsername()
        );

        response.setHeader(SecurityConstants.AUTHORIZATION_HEADER,
                SecurityConstants.TOKEN_PREFIX + accessToken);
        response.setHeader(SecurityConstants.REFRESH_TOKEN_HEADER, refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(
                response.getWriter(),
                EMallsResponseEntity.ok(AuthTokenResponse.bearer(accessToken, refreshToken))
        );

        log.info("User '{}' (role={}) logged in via username/password",
                userDetails.getUsername(), userDetails.getRoleCode());
    }

    private String buildAccessToken(CustomUserDetails userDetails) {
        if (SecurityConstants.ROLE_SHOP_OWNER.equals(userDetails.getRoleCode())) {
            return jwtService.generateAccessToken(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getFullName(),
                    userDetails.getRoleCode(),
                    userDetails.getShopIds()
            );
        }

        if (SecurityConstants.ROLE_CUSTOMER.equals(userDetails.getRoleCode())) {
            return jwtService.generateAccessToken(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getFullName(),
                    userDetails.getRoleCode(),
                    userDetails.getAge(),
                    userDetails.getGender()
            );
        }

        return jwtService.generateAccessToken(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getFullName(),
                userDetails.getRoleCode()
        );
    }
}
