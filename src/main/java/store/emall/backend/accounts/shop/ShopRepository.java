package store.emall.backend.accounts.shop;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {

    @EntityGraph(attributePaths = {"mall", "owner"})
    Optional<Shop> findById(Long id);

    @EntityGraph(attributePaths = {"mall", "owner"})
    List<Shop> findByMall_MallId(Long mallId);

    @EntityGraph(attributePaths = {"mall", "owner"})
    List<Shop> findByOwner_UserId(Long ownerId);

    @EntityGraph(attributePaths = {"mall", "owner"})
    List<Shop> findByMall_MallIdAndStatus(Long mallId, ShopStatus status);

    @EntityGraph(attributePaths = {"mall", "owner"})
    List<Shop> findByMall_MallIdAndStatusAndAdminStatus(
            Long mallId, ShopStatus status, ShopAdminStatus adminStatus);

    @EntityGraph(attributePaths = {"mall", "owner"})
    List<Shop> findByStatusAndAdminStatus(ShopStatus status, ShopAdminStatus adminStatus);

    boolean existsByNameAndMall_MallId(String name, Long mallId);

    boolean existsByNameAndMall_MallIdAndShopIdNot(String name, Long mallId, Long shopId);

    long countByMall_City_CityId(Long cityId);
    long countByStatus(ShopStatus status);
    long countByOwner_UserId(Long ownerId);
    long countByOwner_UserIdAndStatus(Long ownerId, ShopStatus status);
    long countByMall_MallId(Long mallId);
    long countByMall_MallIdAndStatus(Long mallId, ShopStatus status);

    @Query("SELECT s.mall.mallId AS mallId, COUNT(s) AS count FROM Shop s GROUP BY s.mall.mallId")
    List<MallShopCount> countPerMall();

    @Query("SELECT s.mall.mallId AS mallId, COUNT(s) AS count " +
            "FROM Shop s WHERE s.status = 'ACTIVE' GROUP BY s.mall.mallId")
    List<MallShopCount> countActivePerMall();

    @Query("SELECT s.category AS label, COUNT(s) AS value " +
            "FROM Shop s GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<CategoryCount> countByCategory();

    @Query("SELECT m.name AS label, COUNT(s) AS value " +
            "FROM Shop s JOIN s.mall m GROUP BY m.mallId, m.name ORDER BY COUNT(s) DESC")
    List<CategoryCount> countGroupedByMall();

    @Query("SELECT c.name AS label, COUNT(s) AS value " +
            "FROM Shop s JOIN s.mall m JOIN m.city c GROUP BY c.cityId, c.name ORDER BY COUNT(s) DESC")
    List<CategoryCount> countGroupedByCity();

    @Query("SELECT s.createdAt FROM Shop s WHERE s.createdAt IS NOT NULL")
    List<LocalDateTime> findAllCreatedAt();

    @Query("SELECT s.owner.userId AS ownerId, COUNT(s) AS shopCount " +
            "FROM Shop s GROUP BY s.owner.userId ORDER BY COUNT(s) DESC")
    List<OwnerShopCount> countGroupedByOwner(Pageable pageable);

    @Query("SELECT s.status AS status, COUNT(s) AS value " +
            "FROM Shop s WHERE s.owner.userId = :ownerId GROUP BY s.status")
    List<StatusCount> countByOwnerIdGroupByStatus(@Param("ownerId") Long ownerId);


    interface CategoryCount {
        String getLabel();
        long getValue();
    }
    interface StatusCount {
        ShopStatus getStatus();
        long getValue();
    }
    interface OwnerShopCount {
        Long getUserId();
        long getShopCount();
    }
    interface MallShopCount {
        Long getMallId();
        long getCount();
    }
    @Query(value = """
        SELECT *
        FROM accounts.shops s
        WHERE s.logo_uuid = :imageId
           OR s.license_image_uuid = :imageId
           OR EXISTS (
               SELECT 1
               FROM jsonb_array_elements_text(s.shop_photos_uuids) AS img
               WHERE CAST(img AS uuid) = :imageId
           )
        """, nativeQuery = true)
    List<Shop> findByImageId(UUID imageId);
}