package store.emall.backend.catalog.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String q;
    private String slug;
    private Long categoryId;
    private List<Long> categoryIds;
    private Long brandId;
    private Boolean isActive;
    private Long mallId;
    private Long shopId;
    private TargetedAudience targetedAudience;
    private TargetedAudience excludedAudience;
    private AgeGroup ageGroup;
    private Map<Long, List<Long>> selectedOptionsByAttribute;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
