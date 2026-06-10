package store.emall.backend.accounts.mall;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MallRepository extends JpaRepository<Mall, Long>, JpaSpecificationExecutor<Mall> {


    @EntityGraph(attributePaths = {"services", "restaurants", "city"})
    Optional<Mall> findById(Long id);

    @EntityGraph(attributePaths = {"services", "restaurants", "city"})
    List<Mall> findByCity_CityId(Long cityId);

    @EntityGraph(attributePaths = {"services", "restaurants", "city"})
    List<Mall> findByStatus(MallStatus status);

    @EntityGraph(attributePaths = {"services", "restaurants", "city"})
    List<Mall> findByCity_CityIdAndStatus(Long cityId, MallStatus status);

    boolean existsByNameAndCity_CityId(String name, Long cityId);

    boolean existsByNameAndCity_CityIdAndMallIdNot(String name, Long cityId, Long mallId);

    Optional<Mall> findByNameAndCity_CityId(String name, Long cityId);

    long countByStatus(MallStatus status);
    long countByCity_CityId(Long cityId);

    @Query("SELECT m FROM Mall m JOIN FETCH m.city")
    List<Mall> findAllWithCity();

    @Query("SELECT c.name AS label, COUNT(m) AS value " +
            "FROM Mall m JOIN m.city c GROUP BY c.cityId, c.name ORDER BY COUNT(m) DESC")
    List<MallCityCount> countGroupedByCity();

    @Query("SELECT m.status AS status, COUNT(m) AS value FROM Mall m GROUP BY m.status")
    List<MallStatusCount> countByStatusGrouped();

    @Query("SELECT COALESCE(SUM(m.capacity), 0) FROM Mall m")
    long sumCapacity();

    @Query("SELECT m.createdAt FROM Mall m WHERE m.createdAt IS NOT NULL")
    List<LocalDateTime> findAllCreatedAt();

    interface MallCityCount {
        String getLabel();
        long getValue();
    }
    interface MallStatusCount {
        MallStatus getStatus();
        long getValue();
    }


    List<Mall> findByLogoUuid(UUID logoUuid);

    @Query(value = """
    SELECT *
    FROM public.malls m
    WHERE m.logo_uuid = :imageId
       OR EXISTS (
           SELECT 1
           FROM jsonb_array_elements_text(m.mall_images_uuids) AS img
           WHERE CAST(img AS uuid) = :imageId
       )
    """, nativeQuery = true)
    List<Mall> findByImageId(UUID imageId);
}
