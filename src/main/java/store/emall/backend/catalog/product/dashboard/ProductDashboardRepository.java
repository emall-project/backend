package store.emall.backend.catalog.product.dashboard;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductDashboardRepository {
    ProductDashboardKpisDto getKpis(Long shopId);

    ProductDashboardVariantKpisDto getVariantKpis(Long shopId);

    ProductDashboardTagCoverageDto getTagCoverage(Long shopId);

    ProductDashboardPriceStatsDto getPriceStats(Long shopId);

    List<NamedDistributionRowDto> getCategoryDistribution(Long shopId);

    List<NamedDistributionRowDto> getBrandDistribution(Long shopId);

    List<EnumDistributionRowDto> getAudienceDistribution(Long shopId);

    List<EnumDistributionRowDto> getAgeDistribution(Long shopId);

    List<ProductCreatedByMonthDto> getProductsCreatedByMonth(Long shopId, LocalDateTime fromInclusive);
}
