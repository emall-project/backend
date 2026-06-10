package store.emall.backend.campaigns.subscription;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.campaigns.subscription.plan.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_subscriptions", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "shop_subscriptions_audit", schema = "audit")
public class ShopSubscription extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_subscription_seq")
    @SequenceGenerator(
            name = "shop_subscription_seq",
            sequenceName = "shop_subscription_id_seq",
            allocationSize = 1
    )
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "shop_id", nullable = false, unique = true)
    private Long shopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "trial_end_date", nullable = false)
    private LocalDate trialEndDate;

    @Column(name = "price_paid", precision = 12, scale = 2)
    private BigDecimal pricePaid;

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = false;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "suspended_at")
    private LocalDateTime suspendedAt;

    @Column(name = "payment_failure_count", nullable = false)
    private Integer paymentFailureCount = 0;
}