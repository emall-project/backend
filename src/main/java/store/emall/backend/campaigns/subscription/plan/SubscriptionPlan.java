package store.emall.backend.campaigns.subscription.plan;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.campaigns.subscription.SubscriptionPlanType;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "subscription_plans_audit", schema = "audit")
public class SubscriptionPlan extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_plan_seq")
    @SequenceGenerator(
            name = "subscription_plan_seq",
            sequenceName = "subscription_plan_id_seq",
            allocationSize = 1
    )
    @Column(name = "subscription_plan_id")
    private Long subscriptionPlanId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private SubscriptionPlanType planType;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "stripe_price_id", nullable = false)
    private String stripePriceId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}