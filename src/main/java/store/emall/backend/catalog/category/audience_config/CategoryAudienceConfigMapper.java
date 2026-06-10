package store.emall.backend.catalog.category.audience_config;

import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.catalog.category.Category;

public class CategoryAudienceConfigMapper {

    public static CategoryAudienceConfigDto toDto(CategoryAudienceConfig entity) {
        return CategoryAudienceConfigDto.builder()
                .id(entity.getId())
                .ageGroup(entity.getAgeGroup())
                .targetedAudience(entity.getTargetedAudience())
                .imageId(entity.getImageId())
                .build();
    }

    public static CategoryAudienceConfigDto toDto(CategoryAudienceConfig entity, FileDto image) {
        return CategoryAudienceConfigDto.builder()
                .id(entity.getId())
                .ageGroup(entity.getAgeGroup())
                .targetedAudience(entity.getTargetedAudience())
                .imageId(entity.getImageId())
                .image(image)
                .build();
    }

    public static CategoryAudienceConfig toEntity(CategoryAudienceConfigDto dto) {
        return CategoryAudienceConfig.builder()
                .id(dto.getId())
                .ageGroup(dto.getAgeGroup())
                .targetedAudience(dto.getTargetedAudience())
                .imageId(dto.getImageId())
                .build();
    }
    public static CategoryAudienceConfig toEntity(CategoryAudienceConfigDto dto, Category category) {
        return CategoryAudienceConfig.builder()
                .id(dto.getId())
                .category(category)
                .ageGroup(dto.getAgeGroup())
                .targetedAudience(dto.getTargetedAudience())
                .imageId(dto.getImageId())
                .build();
    }

}