package store.emall.backend.campaigns.subscription;

import lombok.*;
import store.emall.backend.campaigns.subscription.plan.SubscriptionPlanDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopSubscriptionDto {
    private Long subscriptionId;
    private Long shopId;
    private SubscriptionPlanDto plan;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate trialEndDate;
    private BigDecimal pricePaid;
    private Boolean autoRenew;
    private LocalDateTime cancelledAt;
    private LocalDateTime suspendedAt;
    private Integer paymentFailureCount;
    private Boolean hasWriteAccess;
}