package store.emall.backend.interaction.models.product_similarity_engine;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.interaction.config.model.ProductSimilarityEngineFeignConfig;
import store.emall.backend.interaction.models.product_similarity_engine.dto.*;
import store.emall.backend.interaction.models.product_similarity_engine.dto.ProductSimilarity;

import java.util.List;

@FeignClient(
        name = "product-similarity-engine",
        url = "${models.product-similarity-engine.url}",
        configuration = ProductSimilarityEngineFeignConfig.class
)
public interface ProductSimilarityEngineClient {

    @GetMapping("/v1/health")
    HealthResponse health();

    @GetMapping("/v1/ready")
    ReadyResponse ready();

    @GetMapping("/v1/index/info")
    IndexInfoResponse indexInfo();

    @GetMapping("/v1/index/stats")
    IndexStatsResponse indexStats();

    @PostMapping("/v1/index/products")
    IndexSingleProductResponse indexProduct(@RequestBody ProductSimilarity product);

    @PostMapping("/v1/index/products:batch")
    AddProductsResponse indexProductsBatch(@RequestBody List<ProductSimilarity> request);

    @PutMapping("/v1/index/products/{productId}")
    UpdateProductResponse upsertIndexedProduct(
            @PathVariable("productId") Long productId,
            @RequestBody ProductSimilarity product
    );

    @DeleteMapping("/v1/index/products/{productId}")
    DeleteProductResponse deleteIndexedProduct(@PathVariable("productId") Long productId);

    @PostMapping("/v1/index:rebuild")
    RebuildIndexResponse rebuildIndex();

    @PostMapping("/v1/index:persist")
    PersistIndexResponse persistIndex();

    @PostMapping("/v1/recommendations/similar")
    SimilarProductsResponse getSimilarProducts(@RequestBody SimilarProductsRequest request);
}