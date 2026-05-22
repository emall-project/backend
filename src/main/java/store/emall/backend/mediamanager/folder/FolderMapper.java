package ps.emall.mediamanager.folder;

import ps.emall.mediamanager.folder.dto.FolderDto;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class FolderMapper {

    public static FolderDto toDto(Folder folder) {
        return Optional.ofNullable(folder)
                .map(f -> FolderDto.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .parentId(f.getParent() != null ? f.getParent().getId() : null)
                        .storeId(f.getStoreId())
                        .scope(f.getScope())
                        .managedBy(f.getManagedBy())
                        .build())
                .orElse(new FolderDto());
    }

    public static Folder toEntity(FolderDto dto, Folder parent) {
        return Optional.ofNullable(dto)
                .map(f -> Folder.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .parent(parent)
                        .storeId(dto.getStoreId())
                        .scope(dto.getScope())
                        .managedBy(dto.getManagedBy())
                        .build())
                .orElse(null);
    }

    public static Folder merge(Folder existing, FolderDto dto, Folder parent) {
        if (existing == null || dto == null) {
            return existing;
        }

        existing.setParent(parent);
        existing.setName(firstNonNull(dto.getName(), existing.getName()));

        return existing;
    }
}