package store.emall.backend.campaigns.offer;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class OfferMapper {

    private OfferMapper() {}

    public static OfferDto toDto(Offer entity) {
        return Optional.ofNullable(entity)
                .map(e -> OfferDto.builder()
                        .offerId(e.getOfferId())
                        .shopId(e.getShopId())
                        .shop(null)
                        .title(e.getTitle())
                        .description(e.getDescription())
                        .discountType(e.getDiscountType())
                        .discountValue(e.getDiscountValue())
                        .startDate(e.getStartDate())
                        .endDate(e.getEndDate())
                        .status(e.getStatus())
                        .maxUses(e.getMaxUses())
                        .currentUses(e.getCurrentUses())
                        .items(e.getItems() != null
                                ? e.getItems().stream()
                                .map(OfferMapper::toItemDto)
                                .collect(Collectors.toList())
                                : null)
                        .build())
                .orElse(null);
    }

    public static OfferItemDto toItemDto(OfferItem item) {
        return Optional.ofNullable(item)
                .map(i -> OfferItemDto.builder()
                        .offerItemId(i.getOfferItemId())
                        .offerId(i.getOffer() != null ? i.getOffer().getOfferId() : null)
                        .productId(i.getProductId())
                        .product(null)
                        .variantPrices(null)
                        .status(i.getStatus())
                        .build())
                .orElse(null);
    }

    public static Offer toEntity(OfferDto dto) {
        return Optional.ofNullable(dto)
                .map(d -> Offer.builder()
                        .shopId(d.getShopId())
                        .title(d.getTitle())
                        .description(d.getDescription())
                        .discountType(d.getDiscountType())
                        .discountValue(d.getDiscountValue())
                        .startDate(d.getStartDate())
                        .endDate(d.getEndDate())
                        .status(OfferStatus.INACTIVE)
                        .maxUses(d.getMaxUses())
                        .currentUses(0)
                        .build())
                .orElse(null);
    }

    public static void merge(Offer existing, OfferDto dto) {
        if (existing == null || dto == null) return;

        existing.setTitle(firstNonNull(dto.getTitle(), existing.getTitle()));
        existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
        existing.setDiscountType(firstNonNull(dto.getDiscountType(), existing.getDiscountType()));
        existing.setDiscountValue(firstNonNull(dto.getDiscountValue(), existing.getDiscountValue()));
        existing.setStartDate(firstNonNull(dto.getStartDate(), existing.getStartDate()));
        existing.setEndDate(firstNonNull(dto.getEndDate(), existing.getEndDate()));
        existing.setMaxUses(firstNonNull(dto.getMaxUses(), existing.getMaxUses()));
    }
}