package ps.emall.mediamanager.folder;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.folder.dto.FolderFilter;

@Component
public final class FolderSpecificationBuilder {

    public Specification<Folder> build(FolderFilter filter) {
        if (filter == null) {
            return Specification.where((Specification<Folder>) null);
        }

        return Specification.allOf(
                nameSpec(filter.getName()),
                parentSpec(filter.getParentId()),
                storeSpec(filter.getStoreId()),
                scopeSpec(filter.getScope()),
                managedBySpec(filter.getManagedByType())
        );
    }

    public static Specification<Folder> nameSpec(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Folder> parentSpec(Long parentId) {
        return (root, query, cb) -> {
            if (parentId == null) {
                return null;
            }

            return cb.equal(root.get("parent").get("id"), parentId);
        };
    }

    public static Specification<Folder> storeSpec(Long storeId) {
        return (root, query, cb) -> {
            if (storeId == null) {
                return null;
            }

            return cb.equal(root.get("storeId"), storeId);
        };
    }

    public static Specification<Folder> scopeSpec(ScopeType scope) {
        return (root, query, cb) -> {
            if (scope == null) {
                return null;
            }

            return cb.equal(root.get("scope"), scope);
        };
    }

    public static Specification<Folder> managedBySpec(ManagedByType managedBy) {
        return (root, query, cb) -> {
            if (managedBy == null) {
                return null;
            }

            return cb.equal(root.get("managedBy"), managedBy);
        };
    }
}
