package store.emall.backend.accounts.request.shopowner;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;
import store.emall.backend.accounts.request.shop.ShopRequest;
import store.emall.backend.accounts.user.Gender;
import store.emall.backend.accounts.user.User;

import java.util.UUID;

@Entity
@Table(name = "shop_owner_requests", schema = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "shop_owner_requests_audit", schema = "accounts")
public class ShopOwnerRequest extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_owner_request_seq")
    @SequenceGenerator(
            name = "shop_owner_request_seq",
            sequenceName = "shop_owner_request_id_seq",
            schema = "accounts",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "national_id_number", unique = true)
    private String nationalIdNumber;

    @Column(name = "profile_picture_uuid")
    private UUID profilePictureUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ShopOwnerRequestStatus status = ShopOwnerRequestStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // After approval, the created user id is stored here
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id")
    private User createdUser;

    // The associated shop request (one-to-one)
    @OneToOne(mappedBy = "shopOwnerRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ShopRequest shopRequest;
}