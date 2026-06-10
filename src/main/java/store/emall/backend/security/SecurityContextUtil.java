package store.emall.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import store.emall.backend.security.dto.StoreRef;
import store.emall.backend.security.userdetails.CustomUserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Static utility for accessing the current authenticated user from the SecurityContext.
 *
 * Usage in code:
 *   Long userId = SecurityContextUtil.getCurrentUserId();
 *   boolean admin  = SecurityContextUtil.isAdmin();
 *
 * Usage in @PreAuthorize SpEL (via @auth bean):
 *   @PreAuthorize("@auth.isAdmin()")
 *   @PreAuthorize("@auth.isOwnerOrAdmin(#userId)")
 */
public final class SecurityContextUtil {

    private SecurityContextUtil() {}

    // ==================== Get Authentication ====================

    public static Optional<Authentication> getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.of(auth);
    }

    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast);
    }

    // ==================== Get User Fields ====================

    public static Long getCurrentUserId() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getUserId)
                .orElseThrow(() -> new SecurityException("User is not authenticated"));
    }

    public static Long getCurrentUserIdOrNull() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getUserId)
                .orElse(null);
    }

    public static String getCurrentUsername() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getUsername)
                .orElseThrow(() -> new SecurityException("User is not authenticated"));
    }

    public static String getCurrentUsernameOrSystem() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getUsername)
                .orElse("SYSTEM");
    }

    public static String getCurrentFullName() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getFullName)
                .orElseThrow(() -> new SecurityException("User is not authenticated"));
    }

    public static String getCurrentPhoneNumber() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getPhoneNumber)
                .orElseThrow(() -> new SecurityException("User is not authenticated"));
    }

    public static String getCurrentRole() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getRoleCode)
                .orElseThrow(() -> new SecurityException("User is not authenticated"));
    }

    /** Returns the current shop owner's shop IDs (empty list for other roles). */
    public static List<StoreRef> getCurrentShopIds() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getShopIds)
                .orElse(Collections.emptyList());
    }
    public static Optional<Integer> getCurrentAge() {
        return getCurrentUserDetails().map(CustomUserDetails::getAge);
    }

    public static Optional<String> getCurrentGender() {
        return getCurrentUserDetails().map(CustomUserDetails::getGender);
    }

    // ==================== Role Checks ====================

    public static boolean isAdmin() {
        return hasAuthority(SecurityConstants.ROLE_ADMIN);
    }

    public static boolean isCustomer() {
        return hasAuthority(SecurityConstants.ROLE_CUSTOMER);
    }

    public static boolean isShopOwner() {
        return hasAuthority(SecurityConstants.ROLE_SHOP_OWNER);
    }

    /**
     * Checks authority by full string (e.g. "ROLE_ADMIN").
     */
    public static boolean hasAuthority(String authority) {
        return getAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(a -> a.equals(authority)))
                .orElse(false);
    }

    /** Checks if user has any of the given full authorities. */
    public static boolean hasAnyAuthority(String... authorities) {
        for (String authority : authorities) {
            if (hasAuthority(authority)) return true;
        }
        return false;
    }

    // ==================== Ownership Checks ====================

    public static boolean isOwner(Long resourceUserId) {
        return getCurrentUserDetails()
                .map(details -> details.getUserId().equals(resourceUserId))
                .orElse(false);
    }

    public static boolean isOwnerOrAdmin(Long resourceUserId) {
        return isAdmin() || isOwner(resourceUserId);
    }

    // ==================== Authentication Checks ====================

    public static boolean isAuthenticated() {
        return getAuthentication().isPresent();
    }
}