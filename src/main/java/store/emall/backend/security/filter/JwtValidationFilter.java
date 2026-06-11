package store.emall.backend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import store.emall.backend.accounts.user.Gender;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.dto.StoreRef;
import store.emall.backend.security.jwt.JwtService;
import store.emall.backend.security.userdetails.CustomUserDetails;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Runs on EVERY request. Extracts the JWT from the Authorization header,
 * validates it, and sets the SecurityContext so downstream filters/controllers
 * know who the authenticated user is.
 *
 * This filter does NOT handle login -- it only validates tokens on protected resources.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip for public URLs
        if (isPublicUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());

        try {
            if (jwtService.isTokenValid(jwt) && jwtService.isAccessToken(jwt)) {
                String username = jwtService.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Long userId = jwtService.extractUserId(jwt);
                    String fullName = jwtService.extractFullName(jwt);
                    String role = jwtService.extractRole(jwt);
                    Integer age = jwtService.extractAge(jwt);
                    Gender gender = jwtService.extractGender(jwt);
                    List<StoreRef> shopIds = jwtService.extractShopIds(jwt);

                    CustomUserDetails userDetails =
                            new CustomUserDetails(userId, username, fullName, role, age, gender, shopIds);

                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        if ("POST".equalsIgnoreCase(method)
                && ("/api/shop-owner-requests".equals(path)
                || path.startsWith("/temps/files/")
                || "/products/all".equals(path)
                || "/products/summary".equals(path)
                || "/products/by-ids".equals(path))) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)
                && (path.startsWith("/api/cities/")
                || path.startsWith("/api/malls/")
                || path.startsWith("/api/shops/")
                || path.startsWith("/api/mall-restaurants/")
                || path.startsWith("/api/mall-services/")
                || path.startsWith("/products/")
                || path.startsWith("/categories/")
                || path.startsWith("/brands/")
                || path.startsWith("/attributes/")
                || path.startsWith("/tags/")
                || "/api/ad-requests/active/displayed".equals(path)
                || "/api/offers/products/active/public".equals(path)
                || path.matches("/api/subscriptions/shop/\\d+/(status|write-access)"))) {
            return true;
        }

        return Arrays.stream(SecurityConstants.PUBLIC_URLS)
                .anyMatch(url -> {
                    if (url.endsWith("/**")) {
                        return path.startsWith(url.replace("/**", ""));
                    }
                    return path.equals(url);
                });
    }

}
