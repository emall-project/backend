package store.emall.backend.campaigns.offer;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.catalog.product.info.ProductInfoDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferItemDto {

    private Long offerItemId;

    private Long offerId;

    private OfferDto offer;

    @NotNull(message = "offerItem.productId.notnull")
    @Positive(message = "offerItem.productId.positive")
    private Long productId;

    private ProductInfoDto product;

    private List<VariantPriceDto> variantPrices;

    private OfferItemStatus status;
}