package store.emall.backend.accounts.request.shop;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopRequestRepository extends JpaRepository<ShopRequest, Long>,
        JpaSpecificationExecutor<ShopRequest> {

    // Used for the new person path(shopOwnerRequest -> ShopRequest)
    Optional<ShopRequest> findByShopOwnerRequest_Id(Long shopOwnerRequestId);

    // Used for the existing user path (User → many ShopRequests)
    List<ShopRequest> findByExistingUser_UserId(Long userId);

    // Used during submission —> block duplicate shop name in same mall (excluding REJECTED)
    boolean existsByNameAndMall_MallIdAndStatusNot(String name, Long mallId, ShopRequestStatus status);

    long countByExistingUserIsNotNullAndStatus(ShopRequestStatus status);
    long countByExistingUserIsNotNull();

    @Query("SELECT r FROM ShopRequest r " +
            "JOIN FETCH r.existingUser " +
            "JOIN FETCH r.mall " +
            "WHERE r.existingUser IS NOT NULL AND r.status = 'PENDING' " +
            "ORDER BY r.createdAt DESC")
    List<ShopRequest> findRecentPendingByExistingOwner(Pageable pageable);

    boolean existsByRequestedMallNameAndRequestedMallCity_CityIdAndStatusNot(
            String requestedMallName,
            Long cityId,
            ShopRequestStatus status);

    @Query(value = """
            SELECT *
            FROM accounts.shop_requests sr
            WHERE sr.logo_uuid = :imageId
               OR sr.license_image_uuid = :imageId
               OR EXISTS (
                   SELECT 1
                   FROM jsonb_array_elements_text(sr.shop_photos_uuids) AS img
                   WHERE CAST(img AS uuid) = :imageId
               )
            """, nativeQuery = true)
    List<ShopRequest> findByImageId(UUID imageId);
}