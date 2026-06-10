package store.emall.backend.orderhub.dashboard;

import lombok.*;
import store.emall.backend.orderhub.dashboard.section.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDashboardDto {
    private Long shopId;
    private ShopOrderKpiDto orderKpis;
    private ShopReturnKpiDto returnKpis;
    private ShopFinanceKpiDto financeKpis;
    private OrderStatusBreakdownDto orderStatusBreakdown;
    private RecentShopOrdersDto recentOrders;
    private PendingReturnsDto pendingReturns;
}