package store.emall.backend.accounts.user.role;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;

@Entity
@Table(name = "roles", schema = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "roles_audit", schema = "accounts")
public class Role extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(
            name = "role_seq",
            sequenceName = "role_id_seq",
            schema = "accounts",
            allocationSize = 1
    )
    @Column(name = "role_id")
    private Long roleId;

    @Column(nullable = false, unique = true, name = "code")
    private String code;

    @Column(name = "name")
    private String name;
}
