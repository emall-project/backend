package store.emall.backend.mediamanager.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID>, JpaSpecificationExecutor<File> {

    boolean existsByNameAndFolder_Id(String name, Long folderId);
    boolean existsByNameAndExtensionAndFolder_Id(String name, String extension, Long folderId);
    List<File> findByFolder_Id(Long folderId);

    Optional<File> findByShopIdAndId(Long shopId, UUID id);

    boolean existsByShopIdAndId(Long shopId, UUID id);

    Optional<File> findByIdAndScope(UUID id, ScopeType scope);

    Optional<File> findByIdAndScopeAndManagedBy(UUID id, ScopeType scope, ManagedByType managedBy);
}