package store.emall.backend.orderhub.cart;

import lombok.*;
import store.emall.backend.common.phone_number.PhoneNumberDto;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDeliveryInfoDto {
    private Long cityId;
    private String deliveryName;
    private PhoneNumberDto deliveryPhone;
    private String deliveryNote;
    private String deliveryLocation;
    private BigDecimal deliveryFee;
}