package store.emall.backend.accounts.mall.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreateServiceDirectly;
import store.emall.backend.common.validation.OnUpdateServiceDirectly;

import java.util.List;

@RestController
@RequestMapping("/mall-services")
@RequiredArgsConstructor
public class MallServiceController {

    private final MallService mallService;

    @GetMapping("/mall/{mallId}")
    public EMallsResponseEntity<List<MallServiceDto>> getServicesByMall(@PathVariable @Positive Long mallId) {
        List<MallServiceDto> services = mallService.getServicesByMall(mallId);
        return EMallsResponseEntity.ok(services);
    }

    @GetMapping("/mall/{mallId}/active")
    public EMallsResponseEntity<List<MallServiceDto>> getActiveServicesByMall(@PathVariable @Positive Long mallId) {
        List<MallServiceDto> services = mallService.getActiveServicesByMall(mallId);
        return EMallsResponseEntity.ok(services);
    }

    @GetMapping("/{serviceId}")
    public EMallsResponseEntity<MallServiceDto> getServiceById(@PathVariable @Positive Long serviceId) {
        MallServiceDto service = mallService.getById(serviceId);
        return EMallsResponseEntity.ok(service);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<MallServiceDto> addService(@RequestBody @Validated({Default.class, OnCreateServiceDirectly.class}) MallServiceDto serviceDto) {
        MallServiceDto dto = mallService.addService(serviceDto);
        return EMallsResponseEntity.created(dto);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Validated({Default.class, OnCreateServiceDirectly.class})
    public EMallsResponseEntity<List<MallServiceDto>> addServices(@RequestBody @Valid List<MallServiceDto> serviceDtos) {
        List<MallServiceDto> dtos = mallService.addServices(serviceDtos);
        return EMallsResponseEntity.created(dtos);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<MallServiceDto> updateService(@RequestBody @Validated({Default.class, OnUpdateServiceDirectly.class}) MallServiceDto serviceDto) {
        MallServiceDto dto = mallService.updateService(serviceDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deleteService(@PathVariable @Positive Long serviceId) {
        mallService.deleteService(serviceId);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/mall/{mallId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deleteAllServices(@PathVariable @Positive Long mallId) {
        mallService.deleteAllServicesByMall(mallId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{serviceId}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> activateService(@PathVariable @Positive Long serviceId) {
        mallService.activateService(serviceId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{serviceId}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deactivateService(@PathVariable @Positive Long serviceId) {
        mallService.deactivateService(serviceId);
        return EMallsResponseEntity.noContent(null);
    }
}