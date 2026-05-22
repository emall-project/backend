package ps.emall.mediamanager.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long>, JpaSpecificationExecutor<Folder> {

    List<Folder> findByParent_Id(Long parentId);

    boolean existsByParent_IdAndName(Long parentId, String name);

    boolean existsByParentIsNullAndName(String name);

    Optional<Folder> findByStoreIdAndId(Long storeId, Long id);

    boolean existsByStoreIdAndId(Long storeId, Long id);

    Optional<Folder> findByIdAndScope(Long id, ScopeType scope);

    Optional<Folder> findByIdAndScopeAndManagedBy(Long id, ScopeType scope, ManagedByType managedBy);

    Optional<Folder> findByIdAndStoreIdAndScopeAndManagedBy(Long id, Long storeId, ScopeType scope, ManagedByType managedBy);

    boolean existsByIdAndScopeAndManagedBy(Long id, ScopeType scope, ManagedByType managedBy);

    Optional<Folder> findByNameAndScopeAndManagedByAndParentIsNull(String name, ScopeType scope, ManagedByType managedBy);
}
