package store.emall.backend.accounts.user;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.accounts.user.role.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "users_audit", schema = "accounts")
public class User extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_id_seq",
            schema = "accounts",
            allocationSize = 1
    )
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "national_id_number", unique = true)
    private String nationalIdNumber;

    @Column(name = "profile_picture_uuid")
    private UUID profilePictureUuid;

    @Column(name = "is_protected", nullable = false)
    @Builder.Default
    private Boolean isProtected = Boolean.FALSE;
}