package store.emall.backend.accounts.mall;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;
import store.emall.backend.accounts.city.City;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.accounts.mall.restaurant.MallRestaurant;
import store.emall.backend.accounts.mall.service.MallServiceEntity;


import java.util.*;

@Entity
@Table(name = "malls", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "malls_audit", schema = "audit")
public class Mall extends EMallsBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_seq")
    @SequenceGenerator(
            name = "mall_seq",
            sequenceName = "mall_id_seq",
            allocationSize = 1
    )
    @Column(name = "mall_id")
    private Long mallId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "location", nullable = false)
    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    private Map<String, Object> contactInfo = new HashMap<>();

    @Column(name = "logo_uuid")
    private UUID logoUuid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mall_images_uuids", columnDefinition = "jsonb")
    @Builder.Default
    private List<UUID> mallImagesUuids = new ArrayList<>();

    @Builder.Default
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MallStatus status = MallStatus.ACTIVE;

    @Builder.Default
    @OneToMany(mappedBy = "mall", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<MallServiceEntity> services = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "mall", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<MallRestaurant> restaurants = new HashSet<>();

}
