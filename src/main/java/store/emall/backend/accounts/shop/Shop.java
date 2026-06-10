package store.emall.backend.accounts.shop;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.user.User;

import java.util.*;

@Entity
@Table(name = "shops", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "shops_audit", schema = "audit")
public class Shop extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq")
    @SequenceGenerator(
            name = "shop_seq",
            sequenceName = "shop_id_seq",
            allocationSize = 1
    )
    @Column(name = "shop_id")
    private Long shopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mall_id", nullable = false)
    private Mall mall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

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
    private Map<String, Object> contactInfo = new HashMap<>();

    @Column(name = "logo_uuid")
    private UUID logoUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShopStatus status = ShopStatus.ACTIVE;

    @Column(name = "license_image_uuid", nullable = false)
    private UUID licenseImageUuid;

    @Column(name = "folder_id")
    private Long folderId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shop_photos_uuids", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<UUID> shopPhotosUuids = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_status", nullable = false)
    @Builder.Default
    private ShopAdminStatus adminStatus = ShopAdminStatus.NONE;

    /**
     * A shop is effectively visible to customers only when:
     * - subscription status is ACTIVE (set by campaigns service)
     * - AND no admin override is blocking or putting it in maintenance
     */
    public boolean isEffectivelyActive() {
        return this.status == ShopStatus.ACTIVE
                && this.adminStatus == ShopAdminStatus.NONE;
    }

    /**
     * Write access is allowed when:
     * - subscription is active/trial (status == ACTIVE)
     * - AND admin hasn't BLOCKED the shop (MAINTENANCE still allows write access)
     */
    public boolean hasWriteAccess() {
        return this.status == ShopStatus.ACTIVE
                && this.adminStatus != ShopAdminStatus.BLOCKED;
    }
}