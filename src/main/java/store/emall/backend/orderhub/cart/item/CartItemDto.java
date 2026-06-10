package store.emall.backend.orderhub.cart.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import store.emall.backend.orderhub.client.accounts.MallInfoDto;
import store.emall.backend.orderhub.client.accounts.ShopInfoDto;
import store.emall.backend.orderhub.client.campaigns.OfferInfoDto;
import store.emall.backend.orderhub.client.catalog.ProductInfoDto;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {

    private Long cartItemId;

    private Long productId;
    private String productName;

    private Long variantId;
    private String variantName;

    private Long storeId;
    private Long mallId;

    private BigDecimal basePrice;
    private BigDecimal discountedPrice;
    private BigDecimal effectiveUnitPrice;

    @NotNull(message = "cartItem.quantity.notnull")
    @Min(value = 1, message = "cartItem.quantity.min")
    private Integer quantity;

    private BigDecimal lineTotal;
    private Long offerId;

    private ProductInfoDto productInfo;
    private ProductInfoDto.VariantPriceInfoDto variantInfo;
    private ShopInfoDto storeInfo;
    private MallInfoDto mallInfo;
    private OfferInfoDto offerInfo;
}
