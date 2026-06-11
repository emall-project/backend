package store.emall.backend.campaigns.offer;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offers", schema = "campaigns")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "offers_audit", schema = "campaigns")
public class Offer extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_seq")
    @SequenceGenerator(
            name = "offer_seq",
            sequenceName = "offer_id_seq",
            schema = "campaigns",
            allocationSize = 1
    )
    @Column(name = "offer_id")
    private Long offerId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OfferStatus status = OfferStatus.INACTIVE;

    // Optional cap on how many times this offer can be applied (null = unlimited)
    @Column(name = "max_uses")
    private Integer maxUses;

    // Tracks total uses — incremented externally when offer is applied
    @Column(name = "current_uses", nullable = false)
    private Integer currentUses = 0;

    @Builder.Default
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OfferItem> items = new ArrayList<>();
}