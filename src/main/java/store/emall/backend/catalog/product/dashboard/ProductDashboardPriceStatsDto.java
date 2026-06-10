package store.emall.backend.catalog.product.dashboard;

import java.math.BigDecimal;

public record ProductDashboardPriceStatsDto(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal avgPrice
) {
}
