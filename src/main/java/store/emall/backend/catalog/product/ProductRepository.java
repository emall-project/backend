package store.emall.backend.catalog.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.catalog.product.light.ProductLightRepository;
import store.emall.backend.catalog.product.summary.ProductSummaryRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product>,
        ProductLightRepository,
        ProductSummaryRepository {
    Optional<Product> findBySlug(String slug);

    boolean existsBySlugAndShopId(String slug, Long shopId);

    long countByCategory_Id(Long categoryId);

    long countByBrand_Id(Long brandId);

    long countByTargetedAudience(TargetedAudience targetedAudience);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.brand.id = :brandId")
    void deactivateByBrandId(Long brandId);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.category.id = :categoryId")
    void deactivateByCategoryId(Long categoryId);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.shopId = :shopId")
    void deactivateByShopId(Long shopId);

    boolean existsByTags_Id(Long tagsId);

    long countByTags_Id(Long tagsId);

    boolean existsBySlugIgnoreCaseAndShopId(String slug, Long shopId);

    @Override
    long count(Specification<Product> spec);

    boolean existsByIdAndIsActiveTrue(Long id);

    List<Long> findIdsBySpecification(Specification<Product> spec);

    Optional<Product> findByShopIdAndSlug(Long shopId, String slug);

    Optional<Product> findByShopIdAndId(Long shopId, Long id);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = true
                WHERE p.category.id = :categoryId
            """)
    void activateProductsByCategoryId(Long categoryId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = false
                WHERE p.category.id = :categoryId
            """)
    void deactivateProductsByCategoryId(Long categoryId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = true
                WHERE p.brand.id = :brandId
            """)
    void activateProductsByBrandId(Long brandId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = false
                WHERE p.brand.id = :brandId
            """)
    void deactivateProductsByBrandId(Long brandId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.defaultVariant.id = :defaultVariantId
                WHERE p.id = :id
            """)
    void updateDefaultVariant(Long id, Long defaultVariantId);


    List<Product> findByIdIn(Collection<Long> ids);

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.isActive = true
              AND p.id <> :productId
              AND p.mallId = :mallId
              AND (p.category.id = :categoryId OR p.brand.id = :brandId)
            ORDER BY
              CASE WHEN p.category.id = :categoryId THEN 0 ELSE 1 END,
              p.id DESC
            """)
    List<Product> findFallbackSimilarProducts(
            Long productId,
            Long mallId,
            Long categoryId,
            Long brandId,
            Pageable pageable
    );

    @Query("""
            SELECT p.id
            FROM Product p
            WHERE p.isActive = true
            ORDER BY function('random')
            """)
    List<Long> findRandomActiveProductIds(Pageable pageable);


    Optional<Product> findByIdAndIsActive(Long id, Boolean isActive);
}
