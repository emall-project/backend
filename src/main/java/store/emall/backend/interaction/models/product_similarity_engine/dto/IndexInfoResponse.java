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
public class IndexInfoResponse {
    private String status;
    private String service;
    private String version;

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("embedding_dimension")
    private Integer embeddingDimension;

    @JsonProperty("indexed_products")
    private Integer indexedProducts;

    @JsonProperty("metadata_count")
    private Integer metadataCount;

    @JsonProperty("save_every_n_products")
    private Integer saveEveryNProducts;

    @JsonProperty("storage_dir")
    private String storageDir;

    @JsonProperty("index_file")
    private String indexFile;

    @JsonProperty("metadata_file")
    private String metadataFile;

    @JsonProperty("last_saved_at")
    private String lastSavedAt;
}