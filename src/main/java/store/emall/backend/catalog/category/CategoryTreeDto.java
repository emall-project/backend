package store.emall.backend.catalog.category;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class CategoryTreeDto {
    private Long id;
    private String name;
    private FileDto image;
    private Integer depthLevel;
    private Long productsCount;

    @Builder.Default
    private List<CategoryTreeDto> children = new ArrayList<>();
}
