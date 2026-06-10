package store.emall.backend.accounts.city;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CityDto> getAll(Pageable pageable, Specification<City> spec) {
        Page<CityDto> cityPage = cityRepository.findAll(spec, pageable)
                .map(CityMapper::toDto);
        return PaginatedResponse.of(cityPage);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CityDto> getAllCities(Specification<City> spec) {
        List<City> cities = (spec == null)
                ? cityRepository.findAll()
                : cityRepository.findAll(spec);

        return cities.stream()
                .map(CityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDto> getActiveCities() {
        return cityRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(CityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CityDto getById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(CityExceptions::cityNotFound);
        return CityMapper.toDto(city);
    }

    @Override
    @Transactional
    public CityDto create(CityDto cityDto) {
        if (cityRepository.existsByName(cityDto.getName())) {
            throw CityExceptions.cityNameExists();
        }

        City city = CityMapper.toEntity(cityDto);
        return CityMapper.toDto(cityRepository.save(city));
    }

    @Override
    @Transactional
    public CityDto update(CityDto cityDto) {
        City existing = cityRepository.findById(cityDto.getCityId())
                .orElseThrow(CityExceptions::cityNotFound);

        if (cityDto.getName() != null
                && !cityDto.getName().equals(existing.getName())
                && cityRepository.existsByNameAndCityIdNot(cityDto.getName(), cityDto.getCityId())) {
            throw CityExceptions.cityNameExists();
        }

        CityMapper.merge(existing, cityDto);
        return CityMapper.toDto(cityRepository.save(existing));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(CityExceptions::cityNotFound);

        if (Boolean.FALSE.equals(city.getIsActive())) {
            return;
        }
        city.setIsActive(Boolean.FALSE);
        cityRepository.save(city);
    }
    @Override
    @Transactional
    public void activate(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(CityExceptions::cityNotFound);

        if (Boolean.TRUE.equals(city.getIsActive())) {
            return;
        }
        city.setIsActive(Boolean.TRUE);
        cityRepository.save(city);
    }


}
