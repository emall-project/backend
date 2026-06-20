package store.emall.backend.mediamanager.file;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.mediamanager.folder.Folder;

import java.util.UUID;

@Entity
@Table(
        name = "files",
        schema = "media_manager",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "folder_id"})
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "files_audit", schema = "media_manager")
public class File extends EMallsBaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @Column(name = "mime_type", length = 20)
    private String mimeType;

    @Column(name = "extension", length = 10)
    private String extension;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "size")
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "error_message")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private ScopeType scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "managed_by", nullable = false, length = 50)
    private ManagedByType managedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    @Builder.Default
    private MediaVisibility visibility = MediaVisibility.PRIVATE;

    @Column(name = "bucket", length = 255)
    private String bucket;

    @Column(name = "cache_control", length = 255)
    private String cacheControl;

    @Column(name = "content_type", length = 255)
    private String contentType;
}
