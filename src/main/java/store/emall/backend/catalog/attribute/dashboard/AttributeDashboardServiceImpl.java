package store.emall.backend.catalog.attribute.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.catalog.attribute.AttributeRepository;
import store.emall.backend.catalog.attribute.attribute_options.AttributeOptionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttributeDashboardServiceImpl implements AttributeDashboardService {

    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final ProductAttributeDistributionRepository productAttributeDistributionRepository;

    @Override
    public AttributeDashboardSummaryDto getSummary() {
        long totalAttributes = attributeRepository.count();
        long totalOptions = attributeOptionRepository.count();

        return AttributeDashboardSummaryDto.builder()
                .totalAttributes(totalAttributes)
                .activeAttributes(attributeRepository.countByIsActive(true))
                .inactiveAttributes(attributeRepository.countByIsActive(false))
                .totalOptions(totalOptions)
                .averageOptionsPerAttribute(calculateAverageOptions(totalOptions, totalAttributes))
                .attributeTypeDistribution(productAttributeDistributionRepository.getAttributeTypeDistribution())
                .productDistributionByAttribute(productAttributeDistributionRepository.getProductDistributionByAttribute())
                .build();
    }

    private double calculateAverageOptions(long totalOptions, long totalAttributes) {
        if (totalAttributes == 0) {
            return 0.0;
        }
        return (double) totalOptions / totalAttributes;
    }
}
