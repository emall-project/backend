package store.emall.backend.campaigns.ad.payment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.campaigns.ad.request.AdRequest;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ad_payments", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "ad_payments_audit", schema = "audit")
public class AdPayment extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ad_payment_seq")
    @SequenceGenerator(
            name = "ad_payment_seq",
            sequenceName = "ad_payment_id_seq",
            allocationSize = 1
    )
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_request_id", nullable = false)
    private AdRequest adRequest;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private AdPaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private AdPaymentRecordStatus paymentStatus;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;
}