package store.emall.backend.campaigns.offer;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveOfferDto {
    private Long offerId;
    private List<VariantDiscountDto> variantPrices;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantDiscountDto {
        private Long variantId;
        private BigDecimal discountedPrice;
        private String discountType;
        private BigDecimal discountValue;
    }
}