package store.emall.backend.accounts.mall;

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
import store.emall.backend.accounts.mall.dtos.MallDto;

import java.util.List;

@RestController
@RequestMapping("/api/malls")
@RequiredArgsConstructor
public class MallController {

    private final MallManagementService mallManagementService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<MallDto>> getAll(Pageable pageable, MallSpec spec) {
        PaginatedResponse<MallDto> malls = mallManagementService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(malls);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<MallDto>> getAllMalls(MallSpec spec) {
        List<MallDto> malls = mallManagementService.getAllMalls(spec);
        return EMallsResponseEntity.ok(malls);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<MallDto> getById(@PathVariable @Positive Long id) {
        MallDto mall = mallManagementService.getById(id);
        return EMallsResponseEntity.ok(mall);
    }

    @GetMapping("/city/{cityId}")
    public EMallsResponseEntity<List<MallDto>> getMallsByCity(@PathVariable @Positive Long cityId) {
        List<MallDto> malls = mallManagementService.getMallsByCity(cityId);
        return EMallsResponseEntity.ok(malls);
    }

    @GetMapping("/city/{cityId}/active")
    public EMallsResponseEntity<List<MallDto>> getActiveMallsByCity(@PathVariable @Positive Long cityId) {
        List<MallDto> malls = mallManagementService.getActiveMallsByCity(cityId);
        return EMallsResponseEntity.ok(malls);
    }

    @GetMapping("/status/{status}")
    public EMallsResponseEntity<List<MallDto>> getMallsByStatus(@PathVariable MallStatus status) {
        List<MallDto> malls = mallManagementService.getMallsByStatus(status);
        return EMallsResponseEntity.ok(malls);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<MallDto> create(@RequestBody @Validated({Default.class, OnCreate.class}) MallDto mall) {
        MallDto dto = mallManagementService.create(mall);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<MallDto> update(@RequestBody @Validated({Default.class, OnUpdate.class}) MallDto mall) {
        MallDto dto = mallManagementService.update(mall);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        mallManagementService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> changeStatus(
            @PathVariable @Positive Long id,
            @RequestParam MallStatus status) {
        mallManagementService.changeStatus(id, status);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> activate(@PathVariable @Positive Long id) {
        mallManagementService.activate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deactivate(@PathVariable @Positive Long id) {
        mallManagementService.deactivate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/maintenance")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> setMaintenance(@PathVariable @Positive Long id) {
        mallManagementService.setMaintenance(id);
        return EMallsResponseEntity.noContent(null);
    }

}
