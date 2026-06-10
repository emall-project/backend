package store.emall.backend.catalog.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.catalog.brand.Brand;
import store.emall.backend.catalog.brand.BrandRepository;
import store.emall.backend.catalog.category.Category;
import store.emall.backend.catalog.category.CategoryRepository;
import store.emall.backend.catalog.category.audience_config.CategoryAudienceConfig;
import store.emall.backend.catalog.category.audience_config.CategoryAudienceConfigRepository;
import store.emall.backend.common.Entity;
import store.emall.backend.catalog.product.product_variant.ProductVariant;
import store.emall.backend.catalog.product.product_variant.ProductVariantRepository;
import store.emall.backend.common.util.media.MediaUsageDto;
import store.emall.backend.common.util.media.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CatalogMediaService {
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryAudienceConfigRepository categoryAudienceConfigRepository;

    public MediaUsageDto getMediumUsage(UUID mediumId) {
        List<Category> categories = categoryRepository.findByImageId(mediumId);
        List<CategoryAudienceConfig> audienceConfigs = categoryAudienceConfigRepository.findByImageId(mediumId);
        List<Brand> brands = brandRepository.findByImageId(mediumId);
        List<ProductVariant> variants = productVariantRepository.findByMediumId(mediumId);

        List<Reference> references = new ArrayList<>();
        boolean inUse = false;

        if (categories.size() > 0) {
            inUse = true;
            for (Category category : categories) {
                Reference reference = Reference.builder()
                        .entity(Entity.CATEGORY)
                        .entityId(category.getId())
                        .entityName(category.getName())
                        .build();
                references.add(reference);
            }
        }

        if (audienceConfigs.size() > 0) {
            inUse = true;
            for (CategoryAudienceConfig audienceConfig : audienceConfigs) {
                Reference reference = Reference.builder()
                        .entity(Entity.CATEGORY_AUDIENCE_CONFIG)
                        .entityId(audienceConfig.getId())
                        .entityName(audienceConfig.getCategory().getName() + " " + audienceConfig.getTargetedAudience() + " " + audienceConfig.getAgeGroup())
                        .build();
                references.add(reference);
            }
        }

        if (brands.size() > 0) {
            inUse = true;
            for (Brand brand : brands) {
                Reference reference = Reference.builder()
                        .entity(Entity.BRAND)
                        .entityId(brand.getId())
                        .entityName(brand.getName())
                        .build();
                references.add(reference);
            }
        }

        if (variants.size() > 0) {
            inUse = true;
            for (ProductVariant variant : variants) {
                Reference reference = Reference.builder()
                        .entity(Entity.PRODUCT_VARIANT)
                        .entityId(variant.getId())
                        .entityName(variant.getName())
                        .build();
                references.add(reference);
            }
        }

        return MediaUsageDto.builder()
                .inUse(inUse)
                .references(references)
                .build();
    }

}
