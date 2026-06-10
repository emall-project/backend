package store.emall.backend.campaigns.ad.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdTemplateRepository extends JpaRepository<AdTemplate, Long>, JpaSpecificationExecutor<AdTemplate> {

    boolean existsByName(String name);

    boolean existsByNameAndAdTemplateIdNot(String name, Long adTemplateId);

    List<AdTemplate> findByStatus(AdTemplateStatus status);

    List<AdTemplate> findByPosition(String position);

    List<AdTemplate> findByPositionAndStatus(String position, AdTemplateStatus status);

    boolean existsByNameAndPosition(String name, String position);

    boolean existsByNameAndPositionAndAdTemplateIdNot(String name, String position, Long adTemplateId);

    Long countByStatus(AdTemplateStatus status);

    Long countByPosition(String position);

    @Query("""
        SELECT t FROM AdTemplate t
        WHERE t.status = :status
    """)
    List<AdTemplate> findByStatusQuery(@Param("status") AdTemplateStatus status);
}