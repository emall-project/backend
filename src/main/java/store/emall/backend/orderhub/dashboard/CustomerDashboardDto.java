package store.emall.backend.orderhub.dashboard;

import lombok.*;
import store.emall.backend.orderhub.dashboard.section.ActiveCartsDto;
import store.emall.backend.orderhub.dashboard.section.ActiveReturnsDto;
import store.emall.backend.orderhub.dashboard.section.CustomerOrderKpiDto;
import store.emall.backend.orderhub.dashboard.section.RecentCustomerOrdersDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDashboardDto {

    private Long customerId;
    private CustomerOrderKpiDto orderKpis;
    private ActiveCartsDto activeCarts;
    private RecentCustomerOrdersDto recentOrders;
    private ActiveReturnsDto activeReturns;
}