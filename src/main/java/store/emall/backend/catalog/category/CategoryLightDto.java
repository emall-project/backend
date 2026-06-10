package store.emall.backend.catalog.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.emall.backend.mediamanager.file.dto.FileLightDto;


@Builder
@Getter
@Setter
public class CategoryLightDto {
    private Long id;
    private String name;
    private FileLightDto image;
    private Integer depthLevel;
}


