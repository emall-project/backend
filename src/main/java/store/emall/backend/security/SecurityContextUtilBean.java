package store.emall.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import store.emall.backend.accounts.user.Gender;
import store.emall.backend.security.dto.ShopRef;

import java.util.List;

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

    public boolean isAdmin() {
        return SecurityContextUtil.isAdmin();
    }

    public boolean isCustomer() {
        return SecurityContextUtil.isCustomer();
    }

    public boolean isShopOwner() {
        return SecurityContextUtil.isShopOwner();
    }

    public boolean isAdminOrShopOwner() {
        return SecurityContextUtil.isAdminOrShopOwner();
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

    public Integer getCurrentAge() {
        return SecurityContextUtil.getCurrentAge().orElse(null);
    }

    public Gender getCurrentGender() {
        return SecurityContextUtil.getCurrentGender().orElse(Gender.NOT_SPECIFIED);
    }

    public List<ShopRef> getCurrentShopIds() {
        return SecurityContextUtil.getCurrentShopIds();
    }

    public Long getMallId(Long shopId) {
        return SecurityContextUtil.getMallId(shopId);
    }

    /**
     * Returns true if the current user is ADMIN OR is the owner of the given shop.
     * Used in @PreAuthorize for shop management endpoints.
     */
    public boolean isShopOwnerOf(Long shopId) {
        return SecurityContextUtil.isShopOwnerOf(shopId);
    }

    public boolean isAdminOrShopOwnerOf(Long shopId) {
        return SecurityContextUtil.isAdminOrShopOwnerOf(shopId);
    }

    public boolean adminOnly() {
        return SecurityContextUtil.isAdmin();
    }
}
