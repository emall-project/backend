package store.emall.backend.campaigns.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShopSubscriptionRepository
        extends JpaRepository<ShopSubscription, Long>,
        JpaSpecificationExecutor<ShopSubscription> {

    Optional<ShopSubscription> findByShopId(Long shopId);

    Optional<ShopSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);

    boolean existsByShopId(Long shopId);

    List<ShopSubscription> findByStatusAndTrialEndDateBefore(
            SubscriptionStatus status, LocalDate date);

    List<ShopSubscription> findByStatusAndEndDateBefore(
            SubscriptionStatus status, LocalDate date);

    List<ShopSubscription> findByStatusAndSuspendedAtBefore(
            SubscriptionStatus status, LocalDateTime dateTime);

    @Query("SELECT s FROM ShopSubscription s WHERE s.status = :status AND s.trialEndDate = :targetDate")
    List<ShopSubscription> findByStatusAndTrialEndDate(
            @Param("status") SubscriptionStatus status,
            @Param("targetDate") LocalDate targetDate);

    @Query("SELECT s FROM ShopSubscription s WHERE s.status = :status AND s.endDate = :targetDate")
    List<ShopSubscription> findByStatusAndEndDate(
            @Param("status") SubscriptionStatus status,
            @Param("targetDate") LocalDate targetDate);
}