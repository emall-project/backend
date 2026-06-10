package store.emall.backend.accounts.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import store.emall.backend.accounts.user.role.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByPhoneNumber(String phone);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByPhoneNumber(String phone);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    List<User> findByIsActiveTrue();
    Long countByRole(Role role);
    List<User> findByLastLoginAtBefore(LocalDateTime date); // users with last login before certain date

    boolean existsByNationalIdNumber(String nationalIdNumber);
    Optional<User> findByNationalIdNumber(String nationalIdNumber);

    long countByIsActive(boolean isActive);
    long countByRole_Code(String roleCode);
    long countByLastLoginAtIsNull();
    long countByLastLoginAtBefore(LocalDateTime date);

    @Query("SELECT u.gender AS gender, COUNT(u) AS value FROM User u GROUP BY u.gender")
    List<GenderCount> countByGenderGrouped();

    @Query("SELECT u.createdAt FROM User u WHERE u.createdAt IS NOT NULL")
    List<LocalDateTime> findAllCreatedAt();

    @Query("SELECT u FROM User u JOIN FETCH u.role r WHERE r.code = 'ROLE_SHOP_OWNER'")
    List<User> findAllShopOwners();

    @Query("SELECT u.age FROM User u WHERE u.age IS NOT NULL")
    List<Integer> findAllAges();

    @Query("SELECT COUNT(u) FROM User u WHERE u.age IS NULL")
    long countWithNoAge();

    interface GenderCount {
        Gender getGender();
        long getValue();
    }

    boolean existsByRole(Role role);


    @Query(value = """
        SELECT *
        FROM public.users u
        WHERE u.profile_picture_uuid = :imageId
        """, nativeQuery = true)
    List<User> findByImageId(UUID imageId);

}
