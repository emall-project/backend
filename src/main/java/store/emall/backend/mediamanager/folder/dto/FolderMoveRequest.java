package ps.emall.mediamanager.folder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderMoveRequest {

    @NotNull(message = "folder.id.notnull")
    private Long id;

    @NotNull(message = "folder.id.newParentId")
    private Long newParentId;
}
