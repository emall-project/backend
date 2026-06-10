package store.emall.backend.orderhub.dashboard.section;

import store.emall.backend.orderhub.cart.CartDto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveCartsDto {
    private List<CartDto> carts; // all ACTIVE carts for this customer
}
