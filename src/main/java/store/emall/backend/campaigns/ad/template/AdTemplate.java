package store.emall.backend.campaigns.ad.template;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.campaigns.ad.request.AdRequest;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ad_templates", schema = "public")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "ad_templates_audit", schema = "audit")
public class AdTemplate extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ad_template_seq")
    @SequenceGenerator(
            name = "ad_template_seq",
            sequenceName = "ad_template_id_seq",
            allocationSize = 1
    )
    @Column(name = "ad_template_id")
    private Long adTemplateId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "image_ratio", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageRatio imageRatio;

    @Column(name = "price_per_hour", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerHour;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AdTemplateStatus status = AdTemplateStatus.ACTIVE;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdRequest> requests = new ArrayList<>();
}
