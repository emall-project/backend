package store.emall.backend.accounts.request.shop;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.accounts.city.CityDto;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.accounts.mall.dtos.MallDto;
import store.emall.backend.accounts.shop.ShopCategory;
import store.emall.backend.accounts.user.UserDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ShopRequestMallValid
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRequestDto {

    @Null(groups = OnCreate.class, message = "shopRequest.id.null")
    @NotNull(groups = OnUpdate.class, message = "shopRequest.id.notnull")
    @Positive(message = "shopRequest.id.positive")
    private Long id;

    @Positive(message = "shopRequest.mallId.positive")
    private Long mallId;

    private MallDto mall;

    private String requestedMallName;
    private Long requestedMallCityId;
    private CityDto requestedMallCity;

    @NotBlank(groups = OnCreate.class, message = "shopRequest.name.notblank")
    @Size(min = 2, max = 255, message = "shopRequest.name.size")
    private String name;

    @NotNull(groups = OnCreate.class, message = "shopRequest.category.notnull")
    private ShopCategory category;

    private String description;

    @NotBlank(groups = OnCreate.class, message = "shopRequest.location.notblank")
    @Size(max = 255, message = "shopRequest.location.size")
    private String location;

    private Map<String, Object> contactInfo;

    private UUID logoUuid;
    private FileDto logoImage;

    @NotNull(groups = OnCreate.class, message = "shopRequest.licenseImageUuid.notnull")
    private UUID licenseImageUuid;
    private FileDto licenseImage;

    @NotEmpty(groups = OnCreate.class, message = "shopRequest.shopPhotosUuids.notempty")
    private List<UUID> shopPhotosUuids;
    private List<FileDto> shopPhotos;


    private ShopRequestStatus status;
    private String rejectionReason;
    private Long createdShopId;

    private Long shopOwnerRequestId;
    private Long existingUserId;
    private UserDto shopOwnerUser;
}