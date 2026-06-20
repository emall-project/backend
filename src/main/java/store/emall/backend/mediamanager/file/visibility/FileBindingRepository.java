package store.emall.backend.mediamanager.file.visibility;

import org.springframework.data.jpa.repository.JpaRepository;
import store.emall.backend.common.EntityType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileBindingRepository extends JpaRepository<FileBinding, Long> {

    Optional<FileBinding> findByFile_IdAndEntityTypeAndEntityIdAndFieldName(
            UUID fileId,
            EntityType entityType,
            String entityId,
            String fieldName
    );

    List<FileBinding> findByEntityTypeAndEntityIdAndFieldName(
            EntityType entityType,
            String entityId,
            String fieldName
    );

    List<FileBinding> findByEntityTypeAndEntityId(EntityType entityType, String entityId);

    boolean existsByFile_IdAndEntityTypeAndEntityId(UUID fileId, EntityType entityType, String entityId);

    long countByFile_IdAndVisibility(UUID fileId, MediaVisibility visibility);

    void deleteByFile_Id(UUID fileId);
}
