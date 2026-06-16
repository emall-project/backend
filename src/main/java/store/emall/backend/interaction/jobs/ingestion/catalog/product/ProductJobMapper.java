package store.emall.backend.interaction.jobs.ingestion.catalog.product;

import store.emall.backend.interaction.models.product_similarity_engine.dto.ProductSimilarity;

public class ProductJobMapper {
    public static ProductSimilarity toProductSimilarity(Product product) {
        if (product == null) {
            return null;
        }
        return ProductSimilarity.builder()
                .id(product.getId())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .targetedAudience(product.getTargetedAudience())
                .ageGroup(product.getAgeGroup())
                .category(product.getCategory())
                .brand(product.getBrand())
                .mallId(product.getMallId())
                .shopId(product.getShopId())
                .isActive(product.getIsActive())
                .build();
    }
}
