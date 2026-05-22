package ps.emall.mediamanager.file;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;

@Component
public final class FileSpecificationBuilder {

    public Specification<File> build(FileFilter filter) {
        if (filter == null) {
            return Specification.where((Specification<File>) null);
        }

        return Specification.allOf(
                nameSpec(filter.getName()),
                mimeTypeSpec(filter.getMimeType()),
                folderSpec(filter.getFolderId()),
                storeSpec(filter.getStoreId()),
                sizeSpec(filter.getFileSize()),
                statusSpec(filter.getStatus()),
                scopeSpec(filter.getScope()),
                managedBySpec(filter.getManagedBy())
        );
    }

    public static Specification<File> nameSpec(String name) {
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

    public static Specification<File> mimeTypeSpec(String mimeType) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(mimeType)) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("mimeType")),
                    "%" + mimeType.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<File> folderSpec(Long folderId) {
        return (root, query, cb) -> {
            if (folderId == null) {
                return null;
            }

            return cb.equal(root.get("folder").get("id"), folderId);
        };
    }

    public static Specification<File> storeSpec(Long storeId) {
        return (root, query, cb) -> {
            if (storeId == null) {
                return null;
            }

            return cb.equal(root.get("storeId"), storeId);
        };
    }

    public static Specification<File> sizeSpec(Long size) {
        return (root, query, cb) -> {
            if (size == null) {
                return null;
            }

            return cb.equal(root.get("size"), size);
        };
    }

    public static Specification<File> statusSpec(Status status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<File> scopeSpec(ScopeType scope) {
        return (root, query, cb) -> {
            if (scope == null) {
                return null;
            }

            return cb.equal(root.get("scope"), scope);
        };
    }

    public static Specification<File> managedBySpec(ManagedByType managedBy) {
        return (root, query, cb) -> {
            if (managedBy == null) {
                return null;
            }

            return cb.equal(root.get("managedBy"), managedBy);
        };
    }
}
