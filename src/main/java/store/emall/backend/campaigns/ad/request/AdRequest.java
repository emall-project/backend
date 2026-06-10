package store.emall.backend.campaigns.ad.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.campaigns.ad.template.AdTemplate;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ad_requests", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "ad_requests_audit", schema = "audit")
public class AdRequest extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ad_request_seq")
    @SequenceGenerator(
            name = "ad_request_seq",
            sequenceName = "ad_request_id_seq",
            allocationSize = 1
    )
    @Column(name = "ad_request_id")
    private Long adRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private AdTemplate template;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AdRequestStatus status = AdRequestStatus.PENDING;

    @Column(name = "payment_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AdPaymentStatus paymentStatus = AdPaymentStatus.UNPAID;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "is_displayed")
    private Boolean isDisplayed = false;

    @Column(name = "payment_reminder_sent")
    private Boolean paymentReminderSent = false;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "ad_request_image_uuid", nullable = false)
    private UUID adRequestImageUuid;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

}
