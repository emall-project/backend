package store.emall.backend.accounts.dashboard;

import store.emall.backend.accounts.dashboard.admin.AdminDashboardDto;
import store.emall.backend.accounts.dashboard.customer.CustomerDashboardDto;
import store.emall.backend.accounts.dashboard.shopowner.ShopOwnerDashboardDto;

public interface DashboardService {

    AdminDashboardDto getAdminDashboard();
    ShopOwnerDashboardDto getShopOwnerDashboard(Long userId);
    CustomerDashboardDto getCustomerDashboard(Long userId);

}
