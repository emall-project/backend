package store.emall.backend.accounts.mall.restaurant;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnCreateRestaurantDirectly;
import store.emall.backend.common.validation.OnUpdateRestaurantDirectly;
import store.emall.backend.accounts.mall.dtos.MallBasicDto;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MallRestaurantDto {

    @Null(groups = OnCreateRestaurantDirectly.class, message = "restaurant.restaurantId.null")
    @NotNull(groups = OnUpdateRestaurantDirectly.class, message = "restaurant.restaurantId.notnull")
    @Positive(message = "restaurant.restaurantId.positive")
    private Long restaurantId;

    @NotNull(groups = OnCreateRestaurantDirectly.class, message = "restaurant.mall.notnull")
    private MallBasicDto mall;

    @NotBlank(groups = {OnCreate.class, OnCreateRestaurantDirectly.class}, message = "restaurant.name.notblank")
    @Size(min = 2, max = 255, message = "restaurant.name.size")
    private String name;

    private String description;

    @Size(max = 100, message = "restaurant.cuisineType.size")
    private String cuisineType;

    @Size(max = 255, message = "restaurant.locationInMall.size")
    private String locationInMall;

    private Map<String, Object> contactInfo;

    private UUID logoUuid;
    private FileDto logoImage;

    private Boolean isActive;
}
