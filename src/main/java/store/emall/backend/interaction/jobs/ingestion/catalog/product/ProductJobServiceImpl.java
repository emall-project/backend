package store.emall.backend.interaction.jobs.ingestion.catalog.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.emall.backend.interaction.analytics.job.JobExecutionRecord;
import store.emall.backend.interaction.analytics.job.JobExecutionService;
import store.emall.backend.interaction.analytics.model.ModelInvocationRecord;
import store.emall.backend.interaction.analytics.model.ModelInvocationService;
import store.emall.backend.common.EntityType;
import store.emall.backend.common.SystemService;
import store.emall.backend.interaction.jobs.JobRoutingKeys;
import store.emall.backend.interaction.models.product_similarity_engine.ProductSimilarityEngineClient;
import store.emall.backend.interaction.models.product_similarity_engine.dto.DeleteProductResponse;
import store.emall.backend.interaction.models.product_similarity_engine.dto.IndexSingleProductResponse;
import store.emall.backend.interaction.models.product_similarity_engine.dto.ProductSimilarity;
import store.emall.backend.interaction.models.product_similarity_engine.dto.UpdateProductResponse;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductJobServiceImpl implements ProductJobService {

    private static final String ENTITY_TYPE = EntityType.PRODUCT.name();
    private static final String SOURCE_SERVICE = SystemService.CATALOG.getServiceName();
    private static final String MODEL_NAME = "product-similarity-engine";// should be more dynamic
    private static final String PROVIDER = "PRODUCT_SIMILARITY_ENGINE";//

    private final ProductSimilarityEngineClient productSimilarityEngineClient;
    private final JobExecutionService jobExecutionService;
    private final ModelInvocationService modelInvocationService;

    @Override
    public void productCreatedJob(Product product) {
        JobExecutionRecord job = jobExecutionService.startJob(
                ProductJobType.PRODUCT_CREATED.name(),
                ENTITY_TYPE,
                product != null ? product.getId() : null,
                SOURCE_SERVICE,
                JobRoutingKeys.CATALOG_PRODUCT_CREATED,
                null,
                Map.of("operation", "index-product")
        );

        if (product == null) {
            jobExecutionService.markSkipped(job, "Product payload is null");
            return;
        }

        ProductSimilarity request = ProductJobMapper.toProductSimilarity(product);
        ModelInvocationRecord invocation = modelInvocationService.startInvocation(
                job,
                MODEL_NAME,
                PROVIDER,
                "indexProduct",
                Map.of("productId", product.getId())
        );

        try {
            IndexSingleProductResponse response = productSimilarityEngineClient.indexProduct(request);

            if (response == null || response.getStatus() == null || !"success".equals(response.getStatus())) {
                modelInvocationService.markFailed(invocation, null, "Invalid response from similarity engine");
                jobExecutionService.markFailed(job, "Product indexing failed");
                return;
            }

            modelInvocationService.markSuccess(invocation, 200);
            jobExecutionService.markSuccess(job);

        } catch (Exception e) {
            modelInvocationService.markFailed(invocation, null, e.getMessage());
            jobExecutionService.markFailed(job, e.getMessage());
            log.error("Could not index product similarity for productId={}", product.getId(), e);
        }
    }

    @Override
    public void productUpdatedJob(Product product) {
        JobExecutionRecord job = jobExecutionService.startJob(
                ProductJobType.PRODUCT_UPDATED.name(),
                ENTITY_TYPE,
                product != null ? product.getId() : null,
                SOURCE_SERVICE,
                JobRoutingKeys.CATALOG_PRODUCT_UPDATED,
                null,
                Map.of("operation", "upsert-product")
        );

        if (product == null) {
            jobExecutionService.markSkipped(job, "Product payload is null");
            return;
        }

        ProductSimilarity request = ProductJobMapper.toProductSimilarity(product);
        ModelInvocationRecord invocation = modelInvocationService.startInvocation(
                job,
                MODEL_NAME,
                PROVIDER,
                "upsertIndexedProduct",
                Map.of("productId", product.getId())
        );

        try {
            UpdateProductResponse response =
                    productSimilarityEngineClient.upsertIndexedProduct(request.getId(), request);

            if (response == null || response.getStatus() == null || !"success".equals(response.getStatus())) {
                modelInvocationService.markFailed(invocation, null, "Invalid response from similarity engine");
                jobExecutionService.markFailed(job, "Product upsert failed");
                return;
            }

            modelInvocationService.markSuccess(invocation, 200);
            jobExecutionService.markSuccess(job);

        } catch (Exception e) {
            modelInvocationService.markFailed(invocation, null, e.getMessage());
            jobExecutionService.markFailed(job, e.getMessage());
            log.error("Could not upsert product similarity for productId={}", product.getId(), e);
        }
    }

    @Override
    public void productDeletedJob(Long productId) {
        JobExecutionRecord job = jobExecutionService.startJob(
                ProductJobType.PRODUCT_DELETED.name(),
                ENTITY_TYPE,
                productId,
                SOURCE_SERVICE,
                JobRoutingKeys.CATALOG_PRODUCT_DELETED,
                null,
                Map.of("operation", "delete-product")
        );

        if (productId == null) {
            jobExecutionService.markSkipped(job, "Product ID is null");
            return;
        }

        ModelInvocationRecord invocation = modelInvocationService.startInvocation(
                job,
                MODEL_NAME,
                PROVIDER,
                "deleteIndexedProduct",
                Map.of("productId", productId)
        );

        try {
            DeleteProductResponse response = productSimilarityEngineClient.deleteIndexedProduct(productId);

            if (response == null || response.getStatus() == null || !"success".equals(response.getStatus())) {
                modelInvocationService.markFailed(invocation, null, "Invalid response from similarity engine");
                jobExecutionService.markFailed(job, "Product delete failed");
                return;
            }

            modelInvocationService.markSuccess(invocation, 200);
            jobExecutionService.markSuccess(job);

        } catch (Exception e) {
            modelInvocationService.markFailed(invocation, null, e.getMessage());
            jobExecutionService.markFailed(job, e.getMessage());
            log.error("Could not delete product similarity for productId={}", productId, e);
        }
    }
}