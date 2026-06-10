package store.emall.backend.orderhub.dashboard;

import store.emall.backend.orderhub.dashboard.section.ProductInsightDto;
import store.emall.backend.orderhub.dashboard.section.ProductOrderRankDto;

import java.util.List;

public interface DashboardService {
    AdminDashboardDto getAdminDashboard();
    ShopDashboardDto getShopDashboard(Long shopId);
    CustomerDashboardDto getCustomerDashboard(Long customerId);
    List<ProductInsightDto> getMostOrderedProducts(Long shopId, Integer limit);
    List<ProductOrderRankDto> getPublicMostOrderedProducts(Integer limit);
    List<ProductInsightDto> getDiscountedOrderedProducts(Long shopId, Integer limit);
}
