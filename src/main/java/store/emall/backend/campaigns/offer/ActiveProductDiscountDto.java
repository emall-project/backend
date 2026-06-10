package store.emall.backend.campaigns.offer;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveProductDiscountDto {

    private Long productId;
    private Long offerId;
    private DiscountType discountType;
    private BigDecimal discountValue;
}