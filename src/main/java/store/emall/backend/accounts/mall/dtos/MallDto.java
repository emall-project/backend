package store.emall.backend.accounts.mall.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.accounts.city.CityDto;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.accounts.mall.MallStatus;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantDto;
import store.emall.backend.accounts.mall.service.MallServiceDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MallDto {

    @Null(groups = OnCreate.class, message = "mall.mallId.null")
    @NotNull(groups = OnUpdate.class, message = "mall.mallId.notnull")
    @Positive(message = "mall.mallId.positive")
    private Long mallId;

    @NotNull(groups = OnCreate.class, message = "mall.cityId.notnull")
    private CityDto city;

    @NotBlank(groups = OnCreate.class, message = "mall.name.notblank")
    @Size(min = 2, max = 255, message = "mall.name.size")
    private String name;

    private String description;

    @Positive(message = "mall.capacity.positive")
    private Integer capacity;

    @NotBlank(groups = OnCreate.class, message = "mall.location.notblank")
    @Size(max = 500, message = "mall.location.size")
    private String location;

    private Map<String, Object> contactInfo;

    private UUID logoUuid;
    private FileDto logoImage;

    private List<UUID> mallImagesUuids;
    private List<FileDto> mallImages;

    private MallStatus status;

    @Valid
    private List<MallServiceDto> services;

    @Valid
    private List<MallRestaurantDto> restaurants;

}
