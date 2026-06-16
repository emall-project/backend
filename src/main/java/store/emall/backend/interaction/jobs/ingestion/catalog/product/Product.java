package store.emall.backend.interaction.jobs.ingestion.catalog.product;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;

    private String name;

    private String slug;

    private String targetedAudience;

    private String ageGroup;

    private Boolean isActive;

    private String shortDescription;

    private String description;

    private String category;

    private String brand;

    private Long mallId;

    private Long shopId;

    private List<String> tags;

    private Map<String, List<String>> attributes;

    private List<UUID> images;

}
