package store.emall.backend.mediamanager.file.visibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.emall.backend.common.EntityType;
import store.emall.backend.mediamanager.file.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileBindingDto {
    private Long id;

    private File file;

    private EntityType entityType;

    private String entityId;

    private String fieldName;

    private MediaVisibility visibility;

}
