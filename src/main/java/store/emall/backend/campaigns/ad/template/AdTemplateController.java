package store.emall.backend.campaigns.ad.template;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import jakarta.validation.groups.Default;
import store.emall.backend.common.validation.OnUpdate;


import java.util.List;


@RestController
@RequestMapping("/ad-templates")
@RequiredArgsConstructor
public class AdTemplateController {

    private final AdTemplateService adTemplateService;

    @GetMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<PaginatedResponse<AdTemplateDto>> getAll(Pageable pageable, AdTemplateSpec spec) {
        PaginatedResponse<AdTemplateDto> templates = adTemplateService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(templates);
    }

    @GetMapping("/all")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdTemplateDto>> getAllTemplates(AdTemplateSpec spec) {
        List<AdTemplateDto> templates = adTemplateService.getAllTemplates(spec);
        return EMallsResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@auth.isAdmin() or @auth.isShopOwner()")
    public EMallsResponseEntity<AdTemplateDto> getById(@PathVariable @Positive Long id) {
        AdTemplateDto template = adTemplateService.getById(id);
        return EMallsResponseEntity.ok(template);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdTemplateDto>> getByStatus(@PathVariable AdTemplateStatus status) {
        List<AdTemplateDto> templates = adTemplateService.getByStatus(status);
        return EMallsResponseEntity.ok(templates);
    }

    @GetMapping("/position/{position}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdTemplateDto>> getByPosition(@PathVariable String position) {
        List<AdTemplateDto> templates = adTemplateService.getByPosition(position);
        return EMallsResponseEntity.ok(templates);
    }

    @GetMapping("/position/{position}/active")
    @PreAuthorize("@auth.isAdmin() or @auth.isShopOwner()")
    public EMallsResponseEntity<List<AdTemplateDto>> getActiveByPosition(@PathVariable String position) {
        List<AdTemplateDto> templates = adTemplateService.getActiveByPosition(position);
        return EMallsResponseEntity.ok(templates);
    }

    @GetMapping("/active")
    @PreAuthorize("@auth.isAdmin() or @auth.isShopOwner()")
    public EMallsResponseEntity<List<AdTemplateDto>> getActiveTemplates() {
        List<AdTemplateDto> templates = adTemplateService.getByStatus(AdTemplateStatus.ACTIVE);
        return EMallsResponseEntity.ok(templates);
    }

    @PostMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<AdTemplateDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) AdTemplateDto dto) {
        AdTemplateDto created = adTemplateService.create(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<AdTemplateDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) AdTemplateDto dto) {
        AdTemplateDto updated = adTemplateService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        adTemplateService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> changeStatus(
            @PathVariable @Positive Long id,
            @RequestParam AdTemplateStatus status) {
        adTemplateService.changeStatus(id, status);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> activate(@PathVariable @Positive Long id) {
        adTemplateService.activate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> archive(@PathVariable @Positive Long id) {
        adTemplateService.archive(id);
        return EMallsResponseEntity.noContent(null);
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Long> countByStatus(@PathVariable AdTemplateStatus status) {
        Long count = adTemplateService.countByStatus(status);
        return EMallsResponseEntity.ok(count);
    }

    @GetMapping("/count/position/{position}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Long> countByPosition(@PathVariable String position) {
        Long count = adTemplateService.countByPosition(position);
        return EMallsResponseEntity.ok(count);
    }
}