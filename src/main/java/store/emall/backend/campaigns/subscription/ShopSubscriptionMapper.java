package store.emall.backend.campaigns.subscription;

import store.emall.backend.campaigns.subscription.plan.SubscriptionPlanMapper;

import java.time.LocalDateTime;
import java.util.Optional;

public class ShopSubscriptionMapper {

    private ShopSubscriptionMapper() {}

    public static ShopSubscriptionDto toDto(ShopSubscription entity) {
        return Optional.ofNullable(entity)
                .map(e -> ShopSubscriptionDto.builder()
                        .subscriptionId(e.getSubscriptionId())
                        .shopId(e.getShopId())
                        .plan(e.getPlan() != null ? SubscriptionPlanMapper.toDto(e.getPlan()) : null)
                        .status(e.getStatus())
                        .startDate(e.getStartDate())
                        .endDate(e.getEndDate())
                        .trialEndDate(e.getTrialEndDate())
                        .pricePaid(e.getPricePaid())
                        .autoRenew(e.getAutoRenew())
                        .cancelledAt(e.getCancelledAt())
                        .suspendedAt(e.getSuspendedAt())
                        .paymentFailureCount(e.getPaymentFailureCount())
                        .hasWriteAccess(computeWriteAccess(e))
                        .build())
                .orElse(null);
    }

    private static Boolean computeWriteAccess(ShopSubscription sub) {
        return switch (sub.getStatus()) {
            case TRIAL, ACTIVE -> true;
            case SUSPENDED -> sub.getSuspendedAt() != null
                    && sub.getSuspendedAt().isAfter(LocalDateTime.now().minusDays(3));
            case EXPIRED, CANCELLED -> false;
        };
    }
}