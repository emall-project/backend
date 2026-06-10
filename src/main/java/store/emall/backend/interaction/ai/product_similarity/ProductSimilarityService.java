package store.emall.backend.interaction.ai.product_similarity;

import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsQuery;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsResult;

public interface ProductSimilarityService {
    SimilarProductsResult getSimilarProducts(SimilarProductsQuery query);
}