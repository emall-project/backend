package ps.emall.mediamanager.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class FileUploadByUrlResponse {
    private UUID fileId;
    private String uploadUrl;
}
