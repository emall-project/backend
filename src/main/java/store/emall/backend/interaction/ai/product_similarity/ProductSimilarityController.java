package store.emall.backend.interaction.ai.product_similarity;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsQuery;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsResult;
import store.emall.backend.common.response.EMallsResponseEntity;

@RestController
@RequestMapping("/ai/product-similarity")
@RequiredArgsConstructor
public class ProductSimilarityController {

    private final ProductSimilarityService productSimilarityService;

    @PostMapping("/similar-products")
    public EMallsResponseEntity<SimilarProductsResult> getSimilarProducts(
            @RequestBody SimilarProductsQuery query
    ) {
        return EMallsResponseEntity.ok(productSimilarityService.getSimilarProducts(query));
    }
}