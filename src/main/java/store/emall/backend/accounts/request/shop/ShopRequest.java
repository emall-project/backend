package store.emall.backend.accounts.request.shop;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;
import store.emall.backend.accounts.city.City;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.request.shopowner.ShopOwnerRequest;
import store.emall.backend.accounts.shop.Shop;
import store.emall.backend.accounts.shop.ShopCategory;
import store.emall.backend.accounts.user.User;

import java.util.*;

@Entity
@Table(name = "shop_requests", schema = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "shop_requests_audit", schema = "accounts")
public class ShopRequest extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_request_seq")
    @SequenceGenerator(
            name = "shop_request_seq",
            sequenceName = "shop_request_id_seq",
            schema = "accounts",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_owner_request_id", nullable = true, unique = true)
    private ShopOwnerRequest shopOwnerRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "existing_user_id", nullable = true)
    private User existingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @Column(name = "requested_mall_name")
    private String requestedMallName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_mall_city_id")
    private City requestedMallCity;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ShopCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", nullable = false)
    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> contactInfo = new HashMap<>();

    @Column(name = "logo_uuid")
    private UUID logoUuid;

    @Column(name = "license_image_uuid", nullable = false)
    private UUID licenseImageUuid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shop_photos_uuids", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<UUID> shopPhotosUuids = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ShopRequestStatus status = ShopRequestStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // After approval, the created shop id is stored here
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_shop_id")
    private Shop createdShop;

}