package ps.emall.mediamanager.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.folder.dto.FolderDto;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FolderServiceHelper {

    private final FolderRepository folderRepository;


    public Folder getFolder(Long id) {
        return folderRepository.findById(id)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public Folder getFolder(Long storeId, Long id) {
        return folderRepository.findByStoreIdAndId(storeId, id)
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

    public Folder getFolder(Long id, Long storeId, ScopeType scope, ManagedByType managedBy) {
        return folderRepository.findByIdAndStoreIdAndScopeAndManagedBy(id, storeId, scope, managedBy)
                .orElseThrow(FolderExceptions::folderNotFound);
    }

    public void validateScopeConsistency(FolderDto dto) {
        if (dto.getScope() == null) {
            throw FolderExceptions.scopeNotFound();
        }

        if (dto.getManagedBy() == null) {
            throw FolderExceptions.managedByNotFound();
        }

        if (dto.getScope() == ScopeType.SYSTEM && dto.getStoreId() != null) {
            throw FolderExceptions.folderScopeMismatch();
        }

        if (dto.getScope() == ScopeType.STORE && dto.getStoreId() == null) {
            throw FolderExceptions.folderScopeMismatch();
        }
    }

    public Folder validateAndLoadParent(Long parentId, Long storeId, ScopeType scope) {
        if (parentId == null) {
            return null;
        }

        Folder parent = getFolder(parentId);

        if (parent.getScope() != scope) {
            throw FolderExceptions.folderScopeMismatch();
        }

        if (scope == ScopeType.SYSTEM) {
            if (parent.getStoreId() != null || storeId != null) {
                throw FolderExceptions.folderScopeMismatch();
            }
            return parent;
        }

        if (!Objects.equals(parent.getStoreId(), storeId)) {
            throw FolderExceptions.storeIdMismatch();
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
            Long storeId
    ) {
        if (folder.getScope() != scope
                || folder.getManagedBy() != managedBy
                || !Objects.equals(folder.getStoreId(), storeId)) {
            throw FolderExceptions.folderNotFound();
        }

        for (Folder child : folderRepository.findByParent_Id(folder.getId())) {
            validateManagedDeleteSubtree(child, scope, managedBy, storeId);
        }
    }
}
