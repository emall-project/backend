package store.emall.backend.catalog.brand;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.common.base.EMallsBaseDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BrandDto extends EMallsBaseDto {
    @Null(groups = OnCreate.class, message = "brand.id.null")
    @NotNull(groups = OnUpdate.class, message = "brand.id.notnull")
    private Long id;

    @NotBlank(message = "brand.name.notblank")
    @Size(min = 3, max = 50, message = "brand.name.size")
    private String name;

    @NotBlank(message = "brand.slug.notblank")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "brand.slug.white.spaces"
    )
    @Pattern(
            regexp = "^(?:[a-z0-9-]|[\\p{IsArabic}&&\\p{L}])+$",
            message = "brand.slug.lowercase"
    )
    @Pattern(
            regexp = "^(?:[a-z]|[\\p{IsArabic}&&\\p{L}]).*(?:[a-z]|[\\p{IsArabic}&&\\p{L}])$",
            message = "category.slug.start.end.letter"
    )
    @Size(min = 3, max = 50, message = "brand.slug.size")
    private String slug;

    @NotNull(message = "brand.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "brand.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "brand.isActive.notnull")
    private Boolean isActive;// todo : shouldn't be "= true;"

    @NotNull(message = "brand.imageId.notnull")
    private UUID imageId;

    FileDto image;

}
