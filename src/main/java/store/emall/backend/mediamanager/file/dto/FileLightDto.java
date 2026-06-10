package store.emall.backend.mediamanager.file.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FileLightDto {
    private UUID id;
    private String smallFileUrl;

}
