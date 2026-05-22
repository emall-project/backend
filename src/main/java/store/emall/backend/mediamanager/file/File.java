package ps.emall.mediamanager.file;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.mediamanager.common.base.EMallsBaseEntity;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;
import ps.emall.mediamanager.folder.Folder;

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
@AuditTable(value = "files_audit", schema = "audit")
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

    @Column(name = "store_id")
    private Long storeId;

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
}