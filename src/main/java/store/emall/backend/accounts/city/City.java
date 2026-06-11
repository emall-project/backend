package store.emall.backend.accounts.city;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "cities", schema = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "cities_audit", schema = "accounts")
public class City extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_seq")
    @SequenceGenerator(
            name = "city_seq",
            sequenceName = "city_id_seq",
            schema = "accounts",
            allocationSize = 1
    )
    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "base_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFee;

    @Column(name = "is_active")
    private Boolean isActive = true;

}
