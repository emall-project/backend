package ps.emall.mediamanager.client.common.dto;


import lombok.*;
import ps.emall.mediamanager.common.SystemService;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reference {
    private SystemService systemService;
    private String entity;
    private Long entityId;
    private String entityName;
}
