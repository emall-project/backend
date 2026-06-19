package store.emall.backend.accounts.shop;

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
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<ShopDto>> getAll(Pageable pageable, ShopSpec spec) {
        PaginatedResponse<ShopDto> shops = shopService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(shops);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<ShopDto>> getAllShops(ShopSpec spec) {
        List<ShopDto> shops = shopService.getAllShops(spec);
        return EMallsResponseEntity.ok(shops);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<ShopDto> getById(@PathVariable @Positive Long id) {
        ShopDto shop = shopService.getById(id);
        return EMallsResponseEntity.ok(shop);
    }

    @GetMapping("/info/{shopId}")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<ShopInfoDto> getByShopId(@PathVariable @Positive Long shopId) {
        ShopInfoDto shop = shopService.getShopById(shopId);
        return EMallsResponseEntity.ok(shop);
    }

    @GetMapping("/{shopId}/active")
    public EMallsResponseEntity<Boolean> isShopActive(@PathVariable @Positive Long shopId) {
        ShopInfoDto shop = shopService.getShopById(shopId);
        return EMallsResponseEntity.ok(shop.getIsActive());
    }

    @GetMapping("/mall/{mallId}")
    public EMallsResponseEntity<List<ShopDto>> getShopsByMall(@PathVariable @Positive Long mallId) {
        List<ShopDto> shops = shopService.getShopsByMall(mallId);
        return EMallsResponseEntity.ok(shops);
    }

    @GetMapping("/mall/{mallId}/active")
    public EMallsResponseEntity<List<ShopDto>> getActiveShopsByMall(@PathVariable @Positive Long mallId) {
        List<ShopDto> shops = shopService.getActiveShopsByMall(mallId);
        return EMallsResponseEntity.ok(shops);
    }

    @GetMapping("/active")
    public EMallsResponseEntity<List<ShopDto>> getActiveShops() {
        List<ShopDto> shops = shopService.getActiveShops();
        return EMallsResponseEntity.ok(shops);
    }

    @GetMapping("/owner/{ownerId}")
    public EMallsResponseEntity<List<ShopDto>> getShopsByOwner(@PathVariable @Positive Long ownerId) {
        List<ShopDto> shops = shopService.getShopsByOwner(ownerId);
        return EMallsResponseEntity.ok(shops);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<ShopDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) ShopDto shop) {
        ShopDto dto = shopService.create(shop);
        return EMallsResponseEntity.created(dto);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @auth.isShopOwnerOf(#shop.shopId)")
    public EMallsResponseEntity<ShopDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) ShopDto shop) {
        ShopDto dto = shopService.update(shop);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        shopService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<Void> activate(@PathVariable @Positive Long id) {
        shopService.activate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<Void> deactivate(@PathVariable @Positive Long id) {
        shopService.deactivate(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/maintenance")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> setMaintenance(@PathVariable @Positive Long id) {
        shopService.setMaintenance(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> adminBlock(@PathVariable @Positive Long id) {
        shopService.adminBlock(id);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> adminClearBlock(@PathVariable @Positive Long id) {
        shopService.adminClearBlock(id);
        return EMallsResponseEntity.noContent(null);
    }

    @GetMapping("/{shopId}/write-access")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<Boolean> hasWriteAccess(@PathVariable @Positive Long shopId) {
        Shop shop = shopService.getShopEntityById(shopId);
        return EMallsResponseEntity.ok(shop.hasWriteAccess());
    }

}