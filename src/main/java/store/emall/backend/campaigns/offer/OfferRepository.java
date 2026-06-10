package store.emall.backend.campaigns.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {

    List<Offer> findByShopId(Long shopId);

    List<Offer> findByStatus(OfferStatus status);

    List<Offer> findByShopIdAndStatus(Long shopId, OfferStatus status);

    boolean existsByTitleAndShopId(String title, Long shopId);

    boolean existsByTitleAndShopIdAndOfferIdNot(String title, Long shopId, Long offerId);

    // Find all INACTIVE offers whose startDate has been reached → activate them
    @Query("SELECT o FROM Offer o WHERE o.status = :status AND o.startDate <= :today AND o.endDate >= :today")
    List<Offer> findOffersToActivate(@Param("status") OfferStatus status, @Param("today") LocalDateTime today);

    // Find all ACTIVE offers whose endDate has passed → expire them
    @Query("SELECT o FROM Offer o WHERE o.status = 'ACTIVE' AND o.endDate < :today")
    List<Offer> findExpiredActiveOffers(@Param("today") LocalDateTime today);

    // Find active offers for a specific shop
    @Query("SELECT o FROM Offer o WHERE o.shopId = :shopId AND o.status = 'ACTIVE' AND o.startDate <= :today AND o.endDate >= :today")
    List<Offer> findActiveOffersForShop(@Param("shopId") Long shopId, @Param("today") LocalDateTime today);

    long countByShopId(Long shopId);

    long countByStatus(OfferStatus status);
}