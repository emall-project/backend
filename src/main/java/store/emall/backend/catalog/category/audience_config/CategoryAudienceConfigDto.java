package store.emall.backend.catalog.category.audience_config;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAudienceConfigDto {
    @Null(groups = OnCreate.class, message = "category.audience.config.id.null")
    @NotNull(groups = OnUpdate.class, message = "category.audience.config.id.notnull")
    private Long id;

    @NotNull(message = "category.audience.config.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "category.audience.config.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "category.audience.config.imageId.notnull")
    private UUID imageId;

    private FileDto image;

}
