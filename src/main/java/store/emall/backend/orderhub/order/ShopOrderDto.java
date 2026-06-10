package store.emall.backend.orderhub.order;

import lombok.*;
import store.emall.backend.orderhub.cart.CartDeliveryInfoDto;
import store.emall.backend.orderhub.client.accounts.MallInfoDto;
import store.emall.backend.orderhub.client.accounts.ShopInfoDto;
import store.emall.backend.orderhub.client.accounts.UserInfoDto;
import store.emall.backend.orderhub.order.item.OrderItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrderDto {
    private Long shopOrderId;
    private Long cartId;
    private Long shopId;
    private Long mallId;
    private Long customerId;

    private BigDecimal total;
    private ShopOrderStatus status;

    private List<OrderItemDto> items;

    private CartDeliveryInfoDto deliveryInfo;
    private String storeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ShopInfoDto shopInfo;
    private MallInfoDto mallInfo;
    private UserInfoDto customerInfo;
}
