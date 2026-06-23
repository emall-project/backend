package store.emall.backend.catalog.product.light;

import lombok.Builder;
import lombok.Data;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.math.BigDecimal;

@Data
@Builder
public class ProductLightDto {
    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private Long defaultVariantId;
    private BigDecimal basePrice;
    private Boolean hasDiscount;
    private BigDecimal discountedPrice;
    private FileDto medium;
    private String categoryName;
    private String brandName;
    private Boolean isActive;
    private Long variantsCount;
}
