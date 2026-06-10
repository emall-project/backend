package store.emall.backend.orderhub.dashboard.section;

import store.emall.backend.orderhub.order.ShopOrderDto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentCustomerOrdersDto {
    private List<ShopOrderDto> orders; // last 10
}
