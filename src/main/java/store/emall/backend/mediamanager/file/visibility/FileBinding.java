package store.emall.backend.mediamanager.file.visibility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.mediamanager.file.File;
import store.emall.backend.common.EntityType;
@Entity
@Table(
        name = "file_bindings",
        schema = "media_manager",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_file_binding_file_entity_field",
                columnNames = {"file_id", "entity_type", "entity_id", "field_name"}
        ),
        indexes = {
                @Index(name = "idx_file_bindings_file_visibility", columnList = "file_id, visibility"),
                @Index(name = "idx_file_bindings_entity", columnList = "entity_type, entity_id, field_name")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FileBinding extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_binding_seq")
    @SequenceGenerator(
            name = "file_binding_seq",
            sequenceName = "file_binding_seq",
            schema = "media_manager",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 80)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false, length = 120)
    private String entityId;

    @Column(name = "field_name", nullable = false, length = 120)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private MediaVisibility visibility;
}
