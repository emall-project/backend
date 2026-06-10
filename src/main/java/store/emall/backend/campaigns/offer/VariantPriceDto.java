package store.emall.backend.campaigns.offer;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantPriceDto {
    private Long variantId;
    private String variantName;
    private BigDecimal originalPrice;    // variant.basePrice
    private BigDecimal discountedPrice;
    private Boolean isDefault;
    private String discountType;
    private BigDecimal discountValue;
}