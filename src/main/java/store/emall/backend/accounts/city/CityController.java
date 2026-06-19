package store.emall.backend.accounts.city;


import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<CityDto>> getAll(Pageable pageable, CitySpec spec) {
        PaginatedResponse<CityDto> cities = cityService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(cities);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<CityDto>> getAllCities(CitySpec spec) {
        List<CityDto> cities = cityService.getAllCities(spec);
        return EMallsResponseEntity.ok(cities);
    }

    @GetMapping("/active")
    public EMallsResponseEntity<List<CityDto>> getActiveCities() {
        List<CityDto> cities = cityService.getActiveCities();
        return EMallsResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<CityDto> getById(@PathVariable @Positive Long id) {
        CityDto city = cityService.getById(id);
        return EMallsResponseEntity.ok(city);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<CityDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) CityDto city) {
        CityDto dto = cityService.create(city);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<CityDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) CityDto city) {
        CityDto dto = cityService.update(city);
        return EMallsResponseEntity.ok(dto);
    }

    @PutMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deactivate(@PathVariable @Positive Long id) {
        cityService.deactivate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> activate(@PathVariable @Positive Long id) {
        cityService.activate(id);
        return EMallsResponseEntity.noContent(null);
    }

}
