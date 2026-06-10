package store.emall.backend.campaigns.offer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import store.emall.backend.accounts.shop.ShopInfoDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferDto {

    @Null(groups = OnCreate.class, message = "offer.offerId.null")
    @NotNull(groups = OnUpdate.class, message = "offer.offerId.notnull")
    @Positive(message = "offer.offerId.positive")
    private Long offerId;

    @NotNull(groups = OnCreate.class, message = "offer.shopId.notnull")
    @Positive(message = "offer.shopId.positive")
    private Long shopId;

    private ShopInfoDto shop;

    @NotBlank(groups = OnCreate.class, message = "offer.title.notblank")
    @Size(min = 2, max = 255, message = "offer.title.size")
    private String title;

    @Size(max = 2000, message = "offer.description.size")
    private String description;

    @NotNull(groups = OnCreate.class, message = "offer.discountType.notnull")
    private DiscountType discountType;

    @NotNull(groups = OnCreate.class, message = "offer.discountValue.notnull")
    @DecimalMin(value = "0.01", message = "offer.discountValue.min")
    private BigDecimal discountValue;

    @NotNull(groups = OnCreate.class, message = "offer.startDate.notnull")
    @FutureOrPresent(message = "offer.startDate.futureOrPresent")
    private LocalDateTime startDate;

    @NotNull(groups = OnCreate.class, message = "offer.endDate.notnull")
    @Future(message = "offer.endDate.future")
    private LocalDateTime endDate;

    private OfferStatus status;

    @Positive(message = "offer.maxUses.positive")
    private Integer maxUses;

    private Integer currentUses;

    //TODO: prevent send it in update
    @NotEmpty(groups = OnCreate.class, message = "offer.items.notempty")
    @Valid
    private List<OfferItemDto> items;
}