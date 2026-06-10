package store.emall.backend.interaction.ai.product_similarity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.emall.backend.interaction.ai.common.ModelProvider;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsQuery;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsResult;
import store.emall.backend.interaction.analytics.job.JobExecutionRecord;
import store.emall.backend.interaction.analytics.job.JobExecutionService;
import store.emall.backend.interaction.analytics.model.ModelInvocationRecord;
import store.emall.backend.interaction.analytics.model.ModelInvocationService;
import store.emall.backend.common.Entity;
import store.emall.backend.common.SystemService;
import store.emall.backend.interaction.jobs.JobRoutingKeys;
import store.emall.backend.interaction.jobs.ingestion.catalog.product.ProductJobType;
import store.emall.backend.interaction.models.product_similarity_engine.ProductSimilarityEngineClient;
import store.emall.backend.interaction.models.product_similarity_engine.dto.SimilarProductsResponse;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSimilarityServiceImpl implements ProductSimilarityService {


    private static final String ENTITY_TYPE = Entity.PRODUCT.name();
    private static final String SOURCE_SERVICE = SystemService.CATALOG.getServiceName();
    private static final String MODEL_NAME = "product-similarity-engine";// should be more dynamic
    private static final String PROVIDER = "PRODUCT_SIMILARITY_ENGINE";//

    private final ProductSimilarityEngineClient productSimilarityEngineClient;
    private final JobExecutionService jobExecutionService;
    private final ModelInvocationService modelInvocationService;

    @Override
    public SimilarProductsResult getSimilarProducts(SimilarProductsQuery query) {
        JobExecutionRecord job = jobExecutionService.startJob(
                ProductJobType.FIND_SIMILAR_PRODUCTS.name(),
                ENTITY_TYPE,
                query.getProductId(),
                SOURCE_SERVICE,
                null,
                null,// todo use the correlation id from the request
                Map.of("operation", "index-product")
        );
        ModelInvocationRecord invocation = modelInvocationService.startInvocation(
                job,
                MODEL_NAME,
                PROVIDER,
                "indexProduct",
                Map.of("productId", query.getProductId())
        );
        try {
            SimilarProductsResponse response = productSimilarityEngineClient.getSimilarProducts(
                    ProductSimilarityMapper.toSimilarProductsRequest(query)
            );


            modelInvocationService.markSuccess(invocation, 200);
            jobExecutionService.markSuccess(job);
            return SimilarProductsResult.builder()
                    .success(true)
                    .provider(ModelProvider.PRODUCT_SIMILARITY_ENGINE.name())
                    .productIds(response != null && response.getSimilarProductIds() != null
                            ? response.getSimilarProductIds()
                            : Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Failed to get similar products for productId={}", query.getProductId(), e);
            modelInvocationService.markFailed(invocation, null, e.getMessage());
            jobExecutionService.markFailed(job, e.getMessage());
            return SimilarProductsResult.builder()
                    .success(false)
                    .provider(ModelProvider.PRODUCT_SIMILARITY_ENGINE.name())
                    .productIds(Collections.emptyList())
                    .build();
        }
    }
}