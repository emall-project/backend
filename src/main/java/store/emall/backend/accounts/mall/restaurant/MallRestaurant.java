package store.emall.backend.accounts.mall.restaurant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.accounts.mall.Mall;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "mall_restaurants", schema = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "mall_restaurants_audit", schema = "accounts")
public class MallRestaurant extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_restaurant_seq")
    @SequenceGenerator(
            name = "mall_restaurant_seq",
            sequenceName = "mall_restaurant_id_seq",
            schema = "accounts",
            allocationSize = 1
    )
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mall_id", nullable = false)
    private Mall mall;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cuisine_type", length = 100)
    private String cuisineType;

    @Column(name = "location_in_mall", length = 255)
    private String locationInMall;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    private Map<String, Object> contactInfo;

    @Column(name = "logo_uuid")
    private UUID logoUuid;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
