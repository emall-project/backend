package store.emall.backend.accounts.mall.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MallRestaurantRepository extends JpaRepository<MallRestaurant, Long>, JpaSpecificationExecutor<MallRestaurant> {

    List<MallRestaurant> findByMall_MallId(Long mallId);

    List<MallRestaurant> findByMall_MallIdAndIsActiveTrue(Long mallId);

    List<MallRestaurant> findByMall_MallIdAndCuisineType(Long mallId, String cuisineType);

    boolean existsByNameAndMall_MallId(String name, Long mallId);

    boolean existsByNameAndMall_MallIdAndRestaurantIdNot(String name, Long mallId, Long restaurantId);

    void deleteByMall_MallId(Long mallId);

    List<MallRestaurant> findByCuisineTypeIgnoreCase(String cuisineType);

    long countByIsActive(boolean isActive);

    @Query("SELECT r.cuisineType AS label, COUNT(r) AS value " +
            "FROM MallRestaurant r WHERE r.cuisineType IS NOT NULL " +
            "GROUP BY r.cuisineType ORDER BY COUNT(r) DESC")
    List<CuisineCount> countByCuisineGrouped();

    @Query("SELECT m.name AS label, COUNT(r) AS value " +
            "FROM MallRestaurant r JOIN r.mall m GROUP BY m.mallId, m.name ORDER BY COUNT(r) DESC")
    List<CuisineCount> countGroupedByMall();

    @Query("SELECT r.mall.mallId AS mallId, COUNT(r) AS count " +
            "FROM MallRestaurant r GROUP BY r.mall.mallId")
    List<MallCount> countPerMall();

    interface CuisineCount {
        String getLabel();
        long getValue();
    }
    interface MallCount {
        Long getMallId();
        long getCount();
    }


    @Query(value = """
        SELECT *
        FROM accounts.mall_restaurants mr
        WHERE mr.logo_uuid = :imageId
        """, nativeQuery = true)
    List<MallRestaurant> findByImageId(UUID imageId);

}
