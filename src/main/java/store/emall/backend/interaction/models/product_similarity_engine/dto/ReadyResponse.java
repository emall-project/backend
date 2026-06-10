package store.emall.backend.interaction.models.product_similarity_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadyResponse {
    private String status;
    private Boolean ready;

    @JsonProperty("indexed_products")
    private Integer indexedProducts;

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("embedding_dimension")
    private Integer embeddingDimension;
}