package store.emall.backend.accounts.city;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface CityService {

    PaginatedResponse<CityDto> getAll(Pageable pageable, Specification<City> spec);

    List<CityDto> getAllCities(Specification<City> spec);
    List<CityDto> getActiveCities();

    CityDto getById(Long id);
    CityDto create(CityDto cityDto);
    CityDto update(CityDto cityDto);

    void deactivate(Long id);
    void activate(Long id);
    //void delete(Long id);
}
