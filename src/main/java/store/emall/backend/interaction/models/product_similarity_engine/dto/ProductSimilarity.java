package store.emall.backend.interaction.models.product_similarity_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSimilarity {
    private Long id;
    private String name;
    private String shortDescription;
    private String description;
    private String targetedAudience;
    private String ageGroup;
    private String category;
    private String brand;
    private Long mallId;
    private Long storeId;
    private Boolean isActive;
}