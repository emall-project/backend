package store.emall.backend.catalog.brand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandFilter {

    private String name;
    private String slug;
    private Boolean isActive;
    private TargetedAudience excludedAudience;
    private TargetedAudience targetedAudience;
    private AgeGroup ageGroup;
}
