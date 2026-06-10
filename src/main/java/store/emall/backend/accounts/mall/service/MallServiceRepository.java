package store.emall.backend.accounts.mall.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MallServiceRepository extends JpaRepository<MallServiceEntity, Long>, JpaSpecificationExecutor<MallServiceEntity> {

    List<MallServiceEntity> findByMall_MallId(Long mallId);

    List<MallServiceEntity> findByMall_MallIdAndIsActiveTrue(Long mallId);

    boolean existsByNameAndMall_MallId(String name, Long mallId);

    boolean existsByNameAndMall_MallIdAndServiceIdNot(String name, Long mallId, Long serviceId);

    void deleteByMall_MallId(Long mallId);

    long countByIsActive(boolean isActive);

    @Query("SELECT s.mall.mallId AS mallId, COUNT(s) AS count " +
            "FROM MallServiceEntity s GROUP BY s.mall.mallId")
    List<MallServiceCount> countPerMall();

    interface MallServiceCount {
        Long getMallId();
        long getCount();
    }

}
