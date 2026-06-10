package store.emall.backend.catalog.product.dashboard;

import java.math.BigDecimal;

public record ProductDashboardVariantKpisDto(
        long totalVariants,
        BigDecimal averageVariantsPerProduct,
        long productsWithSingleVariant,
        long productsWithMultipleVariants
) {
}
