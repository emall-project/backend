package ps.emall.mediamanager.folder;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.mediamanager.common.base.EMallsBaseEntity;
import ps.emall.mediamanager.common.scope.ManagedByType;
import ps.emall.mediamanager.common.scope.ScopeType;

import java.util.List;

@Entity
@Table(name = "folders", schema = "media_manager")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "folders_audit", schema = "audit")
public class Folder extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "folder_seq")
    @SequenceGenerator(
            name = "folder_seq",
            schema = "media_manager",
            sequenceName = "folder_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @Column(name = "store_id")
    private Long storeId;


    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private ScopeType scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "managed_by", nullable = false, length = 50)
    private ManagedByType managedBy;


    @OneToMany(mappedBy = "parent")
    private List<Folder> children;
}