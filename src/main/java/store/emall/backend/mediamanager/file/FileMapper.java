package ps.emall.mediamanager.file;

import ps.emall.mediamanager.file.dto.FileDto;
import ps.emall.mediamanager.file.dto.FileUploadByUrlRequest;
import ps.emall.mediamanager.folder.Folder;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class FileMapper {

    public static FileDto toDto(File file) {
        return Optional.ofNullable(file)
                .map(f -> FileDto.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .folderId(f.getFolder() != null ? f.getFolder().getId() : null)
                        .mimeType(f.getMimeType())
                        .extension(f.getExtension())
                        .size(f.getSize())
                        .status(f.getStatus())
                        .storeId(f.getStoreId())
                        .scope(f.getScope())
                        .managedBy(f.getManagedBy())
                        .build())
                .orElse(new FileDto());
    }

    public static File toEntity(FileDto dto, Folder folder) {
        return Optional.ofNullable(dto)
                .map(f -> File.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .folder(folder)
                        .mimeType(dto.getMimeType())
                        .extension(dto.getExtension())
                        .size(dto.getSize())
                        .storeId(dto.getStoreId())
                        .status(dto.getStatus())
                        .scope(dto.getScope())
                        .managedBy(dto.getManagedBy())
                        .build())
                .orElse(null);
    }

    public static File toEntity(FileUploadByUrlRequest dto, Folder folder) {
        return Optional.ofNullable(dto)
                .map(f -> File.builder()
                        .name(dto.getName())
                        .folder(folder)
                        .storeId(dto.getStoreId())
                        .scope(dto.getScope())
                        .managedBy(dto.getManagedBy())
                        .build())
                .orElse(null);
    }

    public static File merge(File existing, FileDto dto, Folder folder) {
        if (existing == null || dto == null) return existing;

        existing.setFolder(folder);
        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setMimeType(firstNonNull(dto.getMimeType(), existing.getMimeType()));
        existing.setExtension(firstNonNull(dto.getExtension(), existing.getExtension()));

        return existing;
    }
}