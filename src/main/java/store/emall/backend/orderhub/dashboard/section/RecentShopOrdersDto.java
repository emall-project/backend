package store.emall.backend.orderhub.dashboard.section;

import lombok.*;
import store.emall.backend.orderhub.order.ShopOrderDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentShopOrdersDto {
    private List<ShopOrderDto> orders; // last 10
}
