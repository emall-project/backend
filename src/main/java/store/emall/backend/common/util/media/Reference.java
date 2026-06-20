package store.emall.backend.common.util.media;


import lombok.*;
import store.emall.backend.common.EntityType;
import store.emall.backend.common.SystemService;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reference {
    private EntityType entityType;
    private Long entityId;
    private String entityName;
    private SystemService systemService;
}
