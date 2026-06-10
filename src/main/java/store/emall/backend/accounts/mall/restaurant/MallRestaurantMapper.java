package store.emall.backend.accounts.mall.restaurant;

import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.accounts.mall.MallMapper;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class MallRestaurantMapper {

    private MallRestaurantMapper() {}

    public static MallRestaurantDto toFullDto(MallRestaurant entity, FileDto logoImage) {
        return Optional.ofNullable(entity)
                .map(e -> MallRestaurantDto.builder()
                        .restaurantId(e.getRestaurantId())
                        .mall(e.getMall() != null ? MallMapper.toBasicDto(e.getMall()) : null)
                        .name(e.getName())
                        .description(e.getDescription())
                        .cuisineType(e.getCuisineType())
                        .locationInMall(e.getLocationInMall())
                        .contactInfo(e.getContactInfo())
                        .logoUuid(e.getLogoUuid())
                        .logoImage(logoImage)
                        .isActive(e.getIsActive())
                        .build())
                .orElse(null);
    }

    public static MallRestaurantDto toDto(MallRestaurant entity) {
        return toFullDto(entity, null);
    }

    public static MallRestaurant toEntity(MallRestaurantDto dto) {
        return Optional.ofNullable(dto)
                .map(d -> MallRestaurant.builder()
                        .restaurantId(d.getRestaurantId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .cuisineType(d.getCuisineType())
                        .locationInMall(d.getLocationInMall())
                        .contactInfo(d.getContactInfo())
                        .logoUuid(d.getLogoUuid())
                        .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                        .build())
                .orElse(null);
    }

    public static MallRestaurant merge(MallRestaurant existing, MallRestaurantDto dto) {
        if (existing == null || dto == null) {
            return existing;
        }

        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
        existing.setCuisineType(firstNonNull(dto.getCuisineType(), existing.getCuisineType()));
        existing.setLocationInMall(firstNonNull(dto.getLocationInMall(), existing.getLocationInMall()));

        if (dto.getContactInfo() != null) {
            existing.setContactInfo(dto.getContactInfo());
        }

        existing.setLogoUuid(firstNonNull(dto.getLogoUuid(), existing.getLogoUuid()));
        existing.setIsActive(firstNonNull(dto.getIsActive(), existing.getIsActive()));

        return existing;
    }
}
