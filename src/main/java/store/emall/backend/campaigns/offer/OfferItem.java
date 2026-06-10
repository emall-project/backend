package store.emall.backend.campaigns.offer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;

@Entity
@Table(
        name = "offer_items",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_offer_product",
                columnNames = {"offer_id", "product_id"}
        )
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "offer_items_audit", schema = "audit")
public class OfferItem extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_item_seq")
    @SequenceGenerator(
            name = "offer_item_seq",
            sequenceName = "offer_item_id_seq",
            allocationSize = 1
    )
    @Column(name = "offer_item_id")
    private Long offerItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OfferItemStatus status = OfferItemStatus.ACTIVE;
}