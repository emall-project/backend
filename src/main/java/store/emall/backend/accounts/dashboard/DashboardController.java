package store.emall.backend.accounts.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.accounts.dashboard.admin.AdminDashboardDto;
import store.emall.backend.accounts.dashboard.customer.CustomerDashboardDto;
import store.emall.backend.accounts.dashboard.shopowner.ShopOwnerDashboardDto;
import store.emall.backend.security.SecurityContextUtil;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<AdminDashboardDto> adminDashboard() {
        return EMallsResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/shop-owner")
    @PreAuthorize("hasAuthority('ROLE_SHOP_OWNER')")
    public EMallsResponseEntity<ShopOwnerDashboardDto> shopOwnerDashboard() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return EMallsResponseEntity.ok(dashboardService.getShopOwnerDashboard(userId));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public EMallsResponseEntity<CustomerDashboardDto> customerDashboard() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return EMallsResponseEntity.ok(dashboardService.getCustomerDashboard(userId));
    }
}
