package store.emall.backend.catalog.category;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.emall.backend.mediamanager.file.dto.FileLightDto;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class CategoryTreeDto {
    private Long id;
    private String name;
    private FileLightDto image;
    private Integer depthLevel;
    private Long productsCount;

    @Builder.Default
    private List<CategoryTreeDto> children = new ArrayList<>();
}
