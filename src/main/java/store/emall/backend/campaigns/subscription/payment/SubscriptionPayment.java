package store.emall.backend.campaigns.subscription.payment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.campaigns.subscription.ShopSubscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_payments", schema = "campaigns")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "subscription_payments_audit", schema = "campaigns")
public class SubscriptionPayment extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_payment_seq")
    @SequenceGenerator(
            name = "subscription_payment_seq",
            sequenceName = "subscription_payment_id_seq",
            schema = "campaigns",
            allocationSize = 1
    )
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private ShopSubscription subscription;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private SubscriptionPaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private SubscriptionPaymentStatus paymentStatus;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;
}