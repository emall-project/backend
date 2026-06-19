package store.emall.backend.accounts.mall.restaurant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnCreateRestaurantDirectly;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.common.validation.OnUpdateRestaurantDirectly;

import java.util.List;

@RestController
@RequestMapping("/mall-restaurants")
@RequiredArgsConstructor
public class MallRestaurantController {

    private final MallRestaurantService restaurantService;

    @GetMapping("/mall/{mallId}")
    public EMallsResponseEntity<List<MallRestaurantDto>> getRestaurantsByMall(@PathVariable @Positive Long mallId) {
        List<MallRestaurantDto> restaurants = restaurantService.getRestaurantsByMall(mallId);
        return EMallsResponseEntity.ok(restaurants);
    }

    @GetMapping("/mall/{mallId}/active")
    public EMallsResponseEntity<List<MallRestaurantDto>> getActiveRestaurantsByMall(@PathVariable @Positive Long mallId) {
        List<MallRestaurantDto> restaurants = restaurantService.getActiveRestaurantsByMall(mallId);
        return EMallsResponseEntity.ok(restaurants);
    }

    @GetMapping("/cuisine/{cuisineType}")
    public EMallsResponseEntity<List<MallRestaurantDto>> getRestaurantsByCuisine(@PathVariable String cuisineType) {
        List<MallRestaurantDto> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType);
        return EMallsResponseEntity.ok(restaurants);
    }

    @GetMapping("/mall/{mallId}/cuisine/{cuisineType}")
    public EMallsResponseEntity<List<MallRestaurantDto>> getRestaurantsByMallAndCuisine(
            @PathVariable @Positive Long mallId,
            @PathVariable String cuisineType) {
        List<MallRestaurantDto> restaurants = restaurantService.getRestaurantsByMallAndCuisine(mallId, cuisineType);
        return EMallsResponseEntity.ok(restaurants);
    }

    @GetMapping("/{restaurantId}")
    public EMallsResponseEntity<MallRestaurantDto> getRestaurantById(@PathVariable @Positive Long restaurantId) {
        MallRestaurantDto restaurant = restaurantService.getById(restaurantId);
        return EMallsResponseEntity.ok(restaurant);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<MallRestaurantDto> addRestaurant(@RequestBody @Validated({Default.class, OnCreateRestaurantDirectly.class}) MallRestaurantDto restaurantDto) {
        MallRestaurantDto dto = restaurantService.addRestaurant(restaurantDto);
        return EMallsResponseEntity.created(dto);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Validated({Default.class,  OnCreateRestaurantDirectly.class})
    public EMallsResponseEntity<List<MallRestaurantDto>> addRestaurants(@RequestBody @Valid List<MallRestaurantDto> restaurantDtos) {
        List<MallRestaurantDto> dtos = restaurantService.addRestaurants(restaurantDtos);
        return EMallsResponseEntity.created(dtos);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<MallRestaurantDto> updateRestaurant(@RequestBody @Validated({Default.class, OnUpdateRestaurantDirectly.class}) MallRestaurantDto restaurantDto) {
        MallRestaurantDto dto = restaurantService.updateRestaurant(restaurantDto);
        return EMallsResponseEntity.ok(dto);
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deleteRestaurant(@PathVariable @Positive Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/mall/{mallId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deleteAllRestaurants(@PathVariable @Positive Long mallId) {restaurantService.deleteAllRestaurantsByMall(mallId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{restaurantId}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> activateRestaurant(@PathVariable @Positive Long restaurantId) {
        restaurantService.activateRestaurant(restaurantId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{restaurantId}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> deactivateRestaurant(@PathVariable @Positive Long restaurantId) {
        restaurantService.deactivateRestaurant(restaurantId);
        return EMallsResponseEntity.noContent(null);
    }

}