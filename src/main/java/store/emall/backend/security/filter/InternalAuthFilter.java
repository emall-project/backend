package store.emall.backend.security.filter;

import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    @Value("${internal.auth.username}")
    private String internalUsername;

    @Value("${internal.auth.password}")
    private String internalPassword;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug("InternalAuthFilter: {} {}", request.getMethod(), request.getRequestURI());


        boolean internalOnly = isInternalOnlyPath(request);
        boolean mixed        = isMixedPath(request);

        if (!internalOnly && !mixed) {
            // Not an internal path at all — skip this filter
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        boolean hasBasicAuth = authHeader != null && authHeader.startsWith("Basic ");

        if (!hasBasicAuth) {
            if (internalOnly) {
                // Exclusively internal — must have Basic auth
                log.warn("Internal endpoint called without Basic auth: {}", request.getRequestURI());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Internal auth required\"}");
                return;
            } else {
                // Mixed endpoint — no Basic auth, let JWT filter handle it
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Basic auth is present — validate credentials
        try {
            String base64  = authHeader.substring("Basic ".length());
            String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);

            if (parts.length != 2
                    || !internalUsername.equals(parts[0])
                    || !internalPassword.equals(parts[1])) {
                log.warn("Invalid internal credentials for path: {}", request.getRequestURI());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid internal credentials\"}");
                return;
            }
        } catch (Exception e) {
            log.warn("Could not decode Basic auth header: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // Credentials valid — set ROLE_INTERNAL in SecurityContext
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                internalUsername, null,
                List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("Internal auth granted for: {}", request.getRequestURI());

        filterChain.doFilter(request, response);
    }

    private boolean isInternalOnlyPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("GET".equals(method) && path.matches("/api/shops/\\d+/exists"))        return true;
        if ("GET".equals(method) && path.matches("/api/shops/\\d+/active"))        return true;
        if ("PUT".equals(method) && path.matches("/api/shops/\\d+/activate"))      return true;
        if ("PUT".equals(method) && path.matches("/api/shops/\\d+/deactivate"))    return true;
        if ("GET".equals(method) && path.matches("/api/shops/\\d+/write-access"))  return true;
        if ("GET".equals(method) && path.matches("/api/shops/info/\\d+"))          return true;
        if ("GET".equals(method) && path.matches("/media/[0-9a-fA-F\\-]{36}/usage"))          return true;

        return false;
    }

    private boolean isMixedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("GET".equals(method) && path.matches("/api/users/\\d+/info")) return true;

        return false;
    }
}