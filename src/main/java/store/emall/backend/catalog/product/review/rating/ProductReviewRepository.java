package store.emall.backend.catalog.product.review.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProduct_Id(Long productId);

    Optional<ProductReview> findByProduct_IdAndUserId(Long productId, Long userId);

    boolean existsByProduct_IdAndUserId(Long productId, Long userId);

    long countByProduct_Id(Long productId);

    long countByProduct_ShopId(Long shopId);

    List<ProductReview> findTop5ByProduct_ShopIdOrderByCreatedAtDesc(Long shopId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.shopId = :shopId")
    Double findAverageRatingByShopId(@Param("shopId") Long shopId);

    @Query("SELECT COUNT(DISTINCT r.product.id) FROM ProductReview r WHERE r.product.shopId = :shopId")
    long countReviewedProductsByShopId(@Param("shopId") Long shopId);
}
