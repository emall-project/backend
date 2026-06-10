package store.emall.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import store.emall.backend.security.filter.*;
import store.emall.backend.security.handler.*;
import store.emall.backend.security.jwt.JwtService;
import store.emall.backend.security.otp.OtpService;
import store.emall.backend.security.provider.PhoneAuthenticationProvider;
import store.emall.backend.security.provider.UsernamePasswordAuthProvider;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtValidationFilter jwtValidationFilter;
    private final PhoneOtpRequestFilter phoneOtpRequestFilter;
    private final GlobalAuthEntryPoint globalAuthEntryPoint;
    private final GlobalAccessDeniedHandler globalAccessDeniedHandler;

    private final UsernamePasswordAuthProvider usernamePasswordAuthProvider;
    private final PhoneAuthenticationProvider phoneAuthenticationProvider;

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final PhoneAuthSuccessHandler phoneAuthSuccessHandler;
    private final PhoneAuthFailureHandler  phoneAuthFailureHandler;

    private final ObjectMapper objectMapper;
    private final JwtService   jwtService;
    private final OtpService   otpService;

    private final InternalAuthFilter internalAuthFilter;


    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                usernamePasswordAuthProvider,
                phoneAuthenticationProvider
        ));
    }

    @Bean
    public UsernamePasswordLoginFilter usernamePasswordLoginFilter(
            AuthenticationManager authenticationManager) {
        return new UsernamePasswordLoginFilter(
                "/api/auth/login",
                authenticationManager,
                loginSuccessHandler,
                loginFailureHandler,
                objectMapper
        );
    }

    @Bean
    public PhoneOtpVerifyFilter phoneOtpVerifyFilter(
            AuthenticationManager authenticationManager) {
        return new PhoneOtpVerifyFilter(
                "/api/auth/phone/verify-otp",
                authenticationManager,
                phoneAuthSuccessHandler,
                phoneAuthFailureHandler,
                objectMapper,
                jwtService,
                otpService
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsernamePasswordLoginFilter usernamePasswordLoginFilter,
            PhoneOtpVerifyFilter phoneOtpVerifyFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(globalAuthEntryPoint)
                        .accessDeniedHandler(globalAccessDeniedHandler))

                .authorizeHttpRequests(auth -> auth

                        // ── 1. Public auth + docs ──
                        .requestMatchers(SecurityConstants.PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/shop-owner-requests").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password/request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password/resend").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password/reset").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users/*/info").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shops/*/exists").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shops/*/active").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shops/*/write-access").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shops/info/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/shops/*/activate").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/shops/*/deactivate").authenticated()


                        // ── 3. Public GET reads — AFTER specific rules above ──
                        .requestMatchers(HttpMethod.GET, "/api/cities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/malls/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/shops/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mall-restaurants/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mall-services/**").permitAll()

                        // checking if we define login endpoint as public, would that solve the login issue?
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/media/*/usage").authenticated()
                        // ── 4. Everything else requires JWT ──
                        .anyRequest().authenticated()
                )

                // Filter chain order:
                .addFilterBefore(phoneOtpRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(usernamePasswordLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(phoneOtpVerifyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(List.of(
                SecurityConstants.AUTHORIZATION_HEADER,
                SecurityConstants.REFRESH_TOKEN_HEADER,
                SecurityConstants.TEMP_TOKEN_HEADER
        ));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}