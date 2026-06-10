package store.emall.backend.accounts.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {

    Optional<City> findByName(String name);

    boolean existsByName(String name);
    boolean existsByNameAndCityIdNot(String name, Long id);

    List<City> findByIsActiveTrue();
    List<City> findByIsActiveTrueOrderByNameAsc();

    long countByIsActive(boolean isActive);

}
