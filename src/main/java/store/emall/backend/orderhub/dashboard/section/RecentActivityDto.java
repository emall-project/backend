package store.emall.backend.orderhub.dashboard.section;

import java.util.List;
import lombok.*;
import store.emall.backend.orderhub.delivery.DeliveryDto;
import store.emall.backend.orderhub.order.ShopOrderDto;
import store.emall.backend.orderhub.returnrequest.ReturnRequestDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentActivityDto {
    private List<ShopOrderDto> recentOrders;    // last 10 orders
    private List<ReturnRequestDto> recentReturns; // last 10 returns
    private List<DeliveryDto> recentDeliveries;   // last 10 deliveries
}
