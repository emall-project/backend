package store.emall.backend.campaigns.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferItemRepository extends JpaRepository<OfferItem, Long> {

    List<OfferItem> findByOffer_OfferId(Long offerId);

    List<OfferItem> findByOffer_OfferIdAndStatus(Long offerId, OfferItemStatus status);

    boolean existsByOffer_OfferIdAndProductId(Long offerId, Long productId);

    Optional<OfferItem> findByOffer_OfferIdAndProductId(Long offerId, Long productId);

    @Query("""
            SELECT oi FROM OfferItem oi
            JOIN oi.offer o
            WHERE oi.productId = :productId
              AND oi.status = 'ACTIVE'
              AND o.status = 'ACTIVE'
              AND o.startDate <= :today
              AND o.endDate >= :today
            """)
    List<OfferItem> findActiveOfferItemsForProduct(
            @Param("productId") Long productId,
            @Param("today") LocalDateTime today);


    @Query("""
        SELECT oi
        FROM OfferItem oi
        JOIN FETCH oi.offer o
        WHERE oi.productId IN :productIds
          AND oi.status = store.emall.backend.campaigns.offer.OfferItemStatus.ACTIVE
          AND o.status = store.emall.backend.campaigns.offer.OfferStatus.ACTIVE
          AND o.startDate <= :now
          AND o.endDate >= :now
        ORDER BY oi.productId ASC, o.startDate DESC, o.offerId DESC
    """)
    List<OfferItem> findResolvedActiveDiscountItemsForProducts(
            @Param("productIds") List<Long> productIds,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT oi
        FROM OfferItem oi
        JOIN FETCH oi.offer o
        WHERE oi.status = store.emall.backend.campaigns.offer.OfferItemStatus.ACTIVE
          AND o.status = store.emall.backend.campaigns.offer.OfferStatus.ACTIVE
          AND o.startDate <= :now
          AND o.endDate >= :now
        ORDER BY o.startDate DESC, o.offerId DESC, oi.productId ASC
    """)
    List<OfferItem> findPublicActiveOfferItems(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
    SELECT oi
    FROM OfferItem oi
    WHERE oi.productId IN :productIds
      AND (:offerId IS NULL OR oi.offer.offerId <> :offerId)
      AND oi.status <> 'REMOVED'
      AND oi.offer.status <> 'EXPIRED'
      AND oi.offer.startDate <= :newEndDate
      AND oi.offer.endDate >= :newStartDate
""")
    List<OfferItem> findOverlappingOffersForProducts(
            @Param("productIds") List<Long> productIds,
            @Param("offerId") Long offerId,
            @Param("newStartDate") LocalDateTime newStartDate,
            @Param("newEndDate") LocalDateTime newEndDate
    );
}
