package store.emall.backend.catalog.product.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDashboardServiceImpl implements ProductDashboardService {

    private final ProductDashboardRepository productDashboardRepository;

    @Override
    public ProductDashboardSummaryDto getSummary(Long shopId) {
        LocalDate from = LocalDate.now()
                .withDayOfMonth(1)
                .minusMonths(5);

        return new ProductDashboardSummaryDto(
                productDashboardRepository.getKpis(shopId),
                productDashboardRepository.getVariantKpis(shopId),
                productDashboardRepository.getTagCoverage(shopId),
                productDashboardRepository.getPriceStats(shopId),
                productDashboardRepository.getCategoryDistribution(shopId),
                productDashboardRepository.getBrandDistribution(shopId),
                productDashboardRepository.getAudienceDistribution(shopId),
                productDashboardRepository.getAgeDistribution(shopId),
                productDashboardRepository.getProductsCreatedByMonth(shopId, from.atStartOfDay())
        );
    }
}
