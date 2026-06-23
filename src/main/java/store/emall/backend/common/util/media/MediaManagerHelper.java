package store.emall.backend.common.util.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import store.emall.backend.catalog.category.CategoryExceptions;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaManagerHelper {
    private final FileService fileService;

    public Map<UUID, FileDto> getMedia(List<UUID> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }
        List<FileDto> files = fileService.getByIds(imageIds);

        Map<UUID, FileDto> fileDtoMap = new HashMap<>();
        for (FileDto fileDto : files) {
            fileDtoMap.put(fileDto.getId(), fileDto);
        }
        return fileDtoMap;
    }

    public boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }


    public FileDto getAndValidatedImage(UUID imageId) {
        FileDto file = fileService.getById(imageId);

        if (!isImage(file.getMimeType())) {
            throw CategoryExceptions.invalidFileType();
        }

        return file;

    }


    public Map<UUID, FileDto> getLightMedia(List<UUID> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }

        // TODO REPLACE WITH endpoint that's return FileDto
        List<FileDto> files = fileService.getByIds(imageIds);

        //inject image File
        Map<UUID, FileDto> fileDtoMap = new HashMap<>();
        for (FileDto fileDto : files) {
            fileDto.setId(fileDto.getId());
            fileDto.setSmallFileUrl(fileDto.getSmallFileUrl());
            fileDtoMap.put(fileDto.getId(), fileDto);
        }
        return fileDtoMap;
    }


}
