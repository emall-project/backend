package store.emall.backend.campaigns.subscription.plan;

import java.util.Optional;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class SubscriptionPlanMapper {

    private SubscriptionPlanMapper() {}

    public static SubscriptionPlanDto toDto(SubscriptionPlan entity) {
        return Optional.ofNullable(entity)
                .map(e -> SubscriptionPlanDto.builder()
                        .subscriptionPlanId(e.getSubscriptionPlanId())
                        .name(e.getName())
                        .planType(e.getPlanType())
                        .durationMonths(e.getDurationMonths())
                        .price(e.getPrice())
                        .currency(e.getCurrency())
                        .stripePriceId(e.getStripePriceId())
                        .isActive(e.getIsActive())
                        .build())
                .orElse(null);
    }

    public static SubscriptionPlan toEntity(SubscriptionPlanDto dto) {
        return Optional.ofNullable(dto)
                .map(d -> SubscriptionPlan.builder()
                        .name(d.getName())
                        .planType(d.getPlanType())
                        .durationMonths(d.getDurationMonths())
                        .price(d.getPrice())
                        .currency(d.getCurrency())
                        .stripePriceId(d.getStripePriceId())
                        .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                        .build())
                .orElse(null);
    }

    public static SubscriptionPlan merge(SubscriptionPlan existing, SubscriptionPlanDto dto) {
        if (existing == null || dto == null) return existing;
        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setPrice(firstNonNull(dto.getPrice(), existing.getPrice()));
        existing.setCurrency(firstNonNull(dto.getCurrency(), existing.getCurrency()));
        existing.setStripePriceId(firstNonNull(dto.getStripePriceId(), existing.getStripePriceId()));
        existing.setIsActive(firstNonNull(dto.getIsActive(), existing.getIsActive()));
        return existing;
    }
}