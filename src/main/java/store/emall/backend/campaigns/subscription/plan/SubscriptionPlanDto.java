package store.emall.backend.campaigns.subscription.plan;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.campaigns.subscription.SubscriptionPlanType;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanDto {

    @Null(groups = OnCreate.class, message = "subscription.plan.id.null")
    @NotNull(groups = OnUpdate.class, message = "subscription.plan.id.notnull")
    @Positive(message = "subscription.plan.id.positive")
    private Long subscriptionPlanId;

    @NotBlank(groups = OnCreate.class, message = "subscription.plan.name.notblank")
    @Size(max = 100, message = "subscription.plan.name.size")
    private String name;

    @NotNull(groups = OnCreate.class, message = "subscription.plan.type.notnull")
    private SubscriptionPlanType planType;

    @NotNull(groups = OnCreate.class, message = "subscription.plan.durationMonths.notnull")
    @Positive(message = "subscription.plan.durationMonths.positive")
    private Integer durationMonths;

    @NotNull(groups = OnCreate.class, message = "subscription.plan.price.notnull")
    @DecimalMin(value = "0.01", message = "subscription.plan.price.min")
    private BigDecimal price;

    @NotBlank(groups = OnCreate.class, message = "subscription.plan.currency.notblank")
    private String currency;

    @NotBlank(groups = OnCreate.class, message = "subscription.plan.stripePriceId.notblank")
    private String stripePriceId;

    private Boolean isActive;
}