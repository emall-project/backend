package store.emall.backend.accounts.request.shopowner;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShopOwnerRequestRepository
        extends JpaRepository<ShopOwnerRequest, Long>, JpaSpecificationExecutor<ShopOwnerRequest> {

    @EntityGraph(attributePaths = {"shopRequest", "shopRequest.mall"})
    Optional<ShopOwnerRequest> findById(Long id);

    @EntityGraph(attributePaths = {"shopRequest", "shopRequest.mall"})
    List<ShopOwnerRequest> findByStatus(ShopOwnerRequestStatus status);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNationalIdNumber(String nationalIdNumber);

    // Using during new submission -> block only if PENDING or APPROVED (not REJECTED)
    boolean existsByUsernameAndStatusNot(String username, ShopOwnerRequestStatus status);
    boolean existsByEmailAndStatusNot(String email, ShopOwnerRequestStatus status);
    boolean existsByPhoneNumberAndStatusNot(String phoneNumber, ShopOwnerRequestStatus status);
    boolean existsByNationalIdNumberAndStatusNot(String nationalIdNumber, ShopOwnerRequestStatus status);

    // Used to find an existing REJECTED request, so we can reset it (re-apply)
    Optional<ShopOwnerRequest> findByUsernameAndStatus(String username, ShopOwnerRequestStatus status);


    long countByStatus(ShopOwnerRequestStatus status);

    @Query("SELECT r.createdAt FROM ShopOwnerRequest r WHERE r.createdAt IS NOT NULL")
    List<LocalDateTime> findAllCreatedAt();
}