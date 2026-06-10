package store.emall.backend.campaigns.ad.request;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdRequestRepository extends JpaRepository<AdRequest, Long>, JpaSpecificationExecutor<AdRequest> {

    @EntityGraph(attributePaths = {"template"})
    Optional<AdRequest> findById(Long id);

    @EntityGraph(attributePaths = {"template"})
    List<AdRequest> findByShopId(Long shopId);

    @EntityGraph(attributePaths = {"template"})
    List<AdRequest> findByStatus(AdRequestStatus status);

    @EntityGraph(attributePaths = {"template"})
    List<AdRequest> findByShopIdAndStatus(Long shopId, AdRequestStatus status);

    @EntityGraph(attributePaths = {"template"})
    List<AdRequest> findByTemplate_AdTemplateId(Long templateId);

    List<AdRequest> findByTemplate_AdTemplateIdAndStatus(Long templateId, AdRequestStatus status);

    boolean existsByTemplate_AdTemplateIdAndStatus(Long templateId, AdRequestStatus status);

    boolean existsByTemplate_AdTemplateIdAndStatusIn(Long templateId, Collection<AdRequestStatus> statuses);

    boolean existsByTemplate_AdTemplateIdAndShopIdAndStatusIn(Long templateId, Long shopId, Collection<AdRequestStatus> statuses);

    Long countByStatus(AdRequestStatus status);

    Long countByShopId(Long shopId);

    Long countByTemplate_AdTemplateId(Long templateId);

    // Find approved but unpaid requests (for payment reminders)
    @Query("SELECT r FROM AdRequest r JOIN FETCH r.template t " +
            "WHERE r.status = 'APPROVED' AND r.paymentStatus = 'UNPAID' " +
            "AND r.paymentReminderSent = false")
    List<AdRequest> findApprovedUnpaidWithoutReminder();

    // Find approved & paid requests ready to be displayed (request startDate reached)
    @Query("SELECT r FROM AdRequest r JOIN FETCH r.template t " +
            "WHERE r.status = 'APPROVED' AND r.paymentStatus = 'PAID' " +
            "AND r.isDisplayed = false AND r.startDate <= CURRENT_TIMESTAMP")
    List<AdRequest> findPaidReadyToDisplay();

    // Find currently displayed ads whose request endDate has expired
    @Query("SELECT r FROM AdRequest r JOIN FETCH r.template t " +
            "WHERE r.isDisplayed = true AND r.endDate < CURRENT_TIMESTAMP")
    List<AdRequest> findDisplayedButExpired();

    // Find approved & unpaid requests whose startDate has passed (overdue)
    @Query("SELECT r FROM AdRequest r JOIN FETCH r.template t " +
            "WHERE r.status = 'APPROVED' AND r.paymentStatus = 'UNPAID' " +
            "AND r.startDate <= CURRENT_TIMESTAMP")
    List<AdRequest> findApprovedUnpaidOverdue();

    // Find all active (currently displayed) ads
    @Query("SELECT r FROM AdRequest r JOIN FETCH r.template t " +
            "WHERE r.isDisplayed = true AND r.startDate <= CURRENT_TIMESTAMP AND r.endDate >= CURRENT_TIMESTAMP")
    List<AdRequest> findCurrentlyDisplayedAds();

    List<AdRequest> findByAdRequestImageUuid(UUID adRequestImageUuid);

    Optional<AdRequest> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Returns true if any APPROVED request for this template overlaps the given time range.
     *
     * Overlap condition: existingStart < newEnd  AND  existingEnd > newStart
     * This covers all overlap cases: partial left, partial right, contained, and surrounding.
     *
     * Used in:
     *  - createRequest  → block shop owner from submitting into an already-booked slot
     *  - approveRequest → prevent admin from approving a request that conflicts with an existing one
     */
    @Query("SELECT COUNT(r) > 0 FROM AdRequest r " +
            "WHERE r.template.adTemplateId = :templateId " +
            "AND r.status = 'APPROVED' " +
            "AND r.startDate < :endDate " +
            "AND r.endDate > :startDate")
    boolean existsApprovedOverlappingRequest(
            @Param("templateId") Long templateId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Returns all PENDING requests for this template whose time range overlaps the given range.
     *
     * Used in approveRequest to auto-reject only the conflicting pending requests,
     * leaving non-overlapping pending requests intact for the admin to handle separately.
     */
    @Query("SELECT r FROM AdRequest r JOIN FETCH r.template " +
            "WHERE r.template.adTemplateId = :templateId " +
            "AND r.status = 'PENDING' " +
            "AND r.startDate < :endDate " +
            "AND r.endDate > :startDate")
    List<AdRequest> findPendingOverlappingRequests(
            @Param("templateId") Long templateId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}