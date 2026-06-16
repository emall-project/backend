package store.emall.backend.mediamanager.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.folder.dto.FolderDto;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FolderServiceHelper {

    private final FolderRepository folderRepository;


    public Folder getFolder(Long id) {
        return folderRepository.findById(id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long shopId, Long id) {
        return folderRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long id, ScopeType scope) {
        return folderRepository.findByIdAndScope(id, scope)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long id, ScopeType scope, ManagedByType managedBy) {
        return folderRepository.findByIdAndScopeAndManagedBy(id, scope, managedBy)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long id, Long shopId, ScopeType scope, ManagedByType managedBy) {
        return folderRepository.findByIdAndShopIdAndScopeAndManagedBy(id, shopId, scope, managedBy)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public void validateScopeConsistency(FolderDto dto) {
        if (dto.getScope() == null) {
            throw FolderExceptions.scopeNotFound();
        }

        if (dto.getManagedBy() == null) {
            throw FolderExceptions.managedByNotFound();
        }

        if (dto.getScope() == ScopeType.SYSTEM && dto.getShopId() != null) {
            throw FolderExceptions.folderScopeMismatch();
        }

        if (dto.getScope() == ScopeType.SHOP && dto.getShopId() == null) {
            throw FolderExceptions.folderScopeMismatch();
        }
    }

    public Folder validateAndLoadParent(Long parentId, Long shopId, ScopeType scope) {
        if (parentId == null) {
            return null;
        }

        Folder parent = getFolder(parentId);

        if (parent.getScope() != scope) {
            throw FolderExceptions.folderScopeMismatch();
        }

        if (scope == ScopeType.SYSTEM) {
            if (parent.getShopId() != null || shopId != null) {
                throw FolderExceptions.folderScopeMismatch();
            }
            return parent;
        }

        if (!Objects.equals(parent.getShopId(), shopId)) {
            throw FolderExceptions.shopIdMismatch();
        }

        return parent;
    }

    public void validateHierarchy(Folder folder) {
        Long reference = folder.getId();
        Folder current = folder.getParent();

        while (current != null) {
            if (Objects.equals(current.getId(), reference)) {
                throw FolderExceptions.folderHierarchyCyclic();
            }
            current = current.getParent();
        }
    }

    public void validateUniqueName(Long parentId, String name) {
        boolean exists = parentId == null
                ? folderRepository.existsByParentIsNullAndName(name)
                : folderRepository.existsByParent_IdAndName(parentId, name);

        if (exists) {
            throw FolderExceptions.folderNameExists();
        }
    }

    public void validateUniqueNameForUpdate(Folder existing, FolderDto dto) {
        boolean sameName = Objects.equals(existing.getName(), dto.getName());
        boolean sameParent = Objects.equals(
                existing.getParent() != null ? existing.getParent().getId() : null,
                dto.getParentId()
        );

        if (!sameName || !sameParent) {
            validateUniqueName(dto.getParentId(), dto.getName());
        }
    }

    public void validateManagedDeleteSubtree(
            Folder folder,
            ScopeType scope,
            ManagedByType managedBy,
            Long shopId
    ) {
        if (folder.getScope() != scope
                || folder.getManagedBy() != managedBy
                || !Objects.equals(folder.getShopId(), shopId)) {
            throw FolderExceptions.folderNotFound();
        }

        for (Folder child : folderRepository.findByParent_Id(folder.getId())) {
            validateManagedDeleteSubtree(child, scope, managedBy, shopId);
        }
    }
}
