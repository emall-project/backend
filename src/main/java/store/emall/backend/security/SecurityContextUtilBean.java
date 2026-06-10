package store.emall.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import store.emall.backend.accounts.shop.ShopRepository;

/**
 * Spring-managed bean that wraps SecurityContextUtil static methods.
 * Use this in @PreAuthorize SpEL:
 *
 *   @PreAuthorize("@auth.isAdmin()")
 *   @PreAuthorize("@auth.isOwnerOrAdmin(#userId)")
 *   @PreAuthorize("@auth.isShopOwnerOf(#shopId)")
 */
@Component("auth")
@RequiredArgsConstructor
public class SecurityContextUtilBean {

    private final ShopRepository shopRepository;

    public boolean isAdmin() {
        return SecurityContextUtil.isAdmin();
    }

    public boolean isCustomer() {
        return SecurityContextUtil.isCustomer();
    }

    public boolean isShopOwner() {
        return SecurityContextUtil.isShopOwner();
    }

    public boolean isOwner(Long resourceUserId) {
        return SecurityContextUtil.isOwner(resourceUserId);
    }

    public boolean isOwnerOrAdmin(Long resourceUserId) {
        return SecurityContextUtil.isOwnerOrAdmin(resourceUserId);
    }

    public Long getCurrentUserId() {
        return SecurityContextUtil.getCurrentUserId();
    }

    public String getCurrentUsername() {
        return SecurityContextUtil.getCurrentUsername();
    }

    /**
     * Returns true if the current user is ADMIN OR is the owner of the given shop.
     * Used in @PreAuthorize for shop management endpoints.
     */
    public boolean isShopOwnerOf(Long shopId) {
        if (SecurityContextUtil.isAdmin()) return true;
        Long currentUserId = SecurityContextUtil.getCurrentUserId();
        return shopRepository.findById(shopId)
                .map(shop -> shop.getOwner().getUserId().equals(currentUserId))
                .orElse(false);
    }
}