package store.emall.backend.catalog.product;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.catalog.brand.Brand;
import store.emall.backend.catalog.category.Category;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.catalog.product.product_variant.ProductVariant;
import store.emall.backend.catalog.tag.Tag;

import java.util.*;

@Entity
@Table(
        name = "products",
        schema = "catalog",
        indexes = {
                @Index(
                        name = "idx_product_mall_category_target_age",
                        columnList = "mall_id, category_id, targeted_audience, age_group"
                ),
                @Index(
                        name = "idx_product_mall_shop_target_age",
                        columnList = "mall_id, shop_id, targeted_audience, age_group"
                ),
                @Index(
                        name = "idx_product_mall_brand_target_age",
                        columnList = "mall_id, brand_id, targeted_audience, age_group"
                ),
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "products_audit", schema = "catalog")
public class Product extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_sequence")
    @SequenceGenerator(
            name = "products_sequence",
            sequenceName = "products_sequence",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "slug", nullable = false, length = 50)
    private String slug;

    @Column(name = "targeted_audience", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetedAudience targetedAudience;

    @Column(name = "age_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "short_description", nullable = false, length = 100)
    private String shortDescription;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "mall_id", nullable = false)
    private Long mallId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @ManyToMany
    @Audited
    @JoinTable(
            name = "product_tags",
            schema = "catalog",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @AuditJoinTable(
            name = "product_tags_audit",
            schema = "catalog"
    )
    private List<Tag> tags = new ArrayList<>();


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
//
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_id")
    private ProductVariant defaultVariant;


    public void addVariant(ProductVariant variant) {
        if (this.variants == null) {
            this.variants = new ArrayList<>();
        }
        variants.add(variant);
        variant.setProduct(this);
    }
}
