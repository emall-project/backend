package store.emall.backend.interaction.ai.product_similarity;

import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsQuery;
import store.emall.backend.interaction.models.product_similarity_engine.dto.SimilarProductsRequest;

public final class ProductSimilarityMapper {

    private ProductSimilarityMapper() {
    }

    public static SimilarProductsRequest toSimilarProductsRequest(SimilarProductsQuery query) {
        return SimilarProductsRequest.builder()
                .productId(query.getProductId())
                .topK(query.getTopK() == null ? 5 : query.getTopK())
                .sameMallOnly(query.getSameMallOnly() == null ? true : query.getSameMallOnly())
                .inStockOnly(query.getInStockOnly() == null ? true : query.getInStockOnly())
                .activeOnly(query.getActiveOnly() == null ? true : query.getActiveOnly())
                .build();
    }
}