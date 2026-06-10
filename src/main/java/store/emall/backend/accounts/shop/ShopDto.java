package store.emall.backend.accounts.shop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.accounts.mall.dtos.MallBasicDto;
import store.emall.backend.accounts.user.UserBasicDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDto {

    @Null(groups = OnCreate.class, message = "shop.shopId.null")
    @NotNull(groups = OnUpdate.class, message = "shop.shopId.notnull")
    @Positive(message = "shop.shopId.positive")
    private Long shopId;

    @NotNull(groups = OnCreate.class, message = "shop.mall.notnull")
    @Valid
    private MallBasicDto mall;

    @NotNull(groups = OnCreate.class, message = "shop.owner.notnull")
    @Valid
    private UserBasicDto owner;

    @NotBlank(groups = OnCreate.class, message = "shop.name.notblank")
    @Size(min = 2, max = 255, message = "shop.name.size")
    private String name;

    @NotNull(groups = OnCreate.class, message = "shop.category.notnull")
    private ShopCategory category;

    private String description;

    @NotBlank(groups = OnCreate.class, message = "shop.location.notblank")
    @Size(max = 255, message = "shop.location.size")
    private String location;

    private Map<String, Object> contactInfo;
    private ShopStatus status;

    private UUID logoUuid;
    private FileDto logoImage;

    @NotNull(groups = OnCreate.class, message = "shop.licenseImageUuid.notnull")
    private UUID licenseImageUuid;
    private FileDto licenseImage;

    @NotEmpty(groups = OnCreate.class, message = "shop.shopPhotosUuids.notempty")
    private List<UUID> shopPhotosUuids;
    private List<FileDto> shopPhotos;

    private Long folderId;
    private ShopAdminStatus adminStatus;
}