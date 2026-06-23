package store.emall.backend.mediamanager.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.mediamanager.file.FileExceptions;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static store.emall.backend.mediamanager.file.util.FileHelper.isImage;

@Service
@RequiredArgsConstructor
public class MediaValidationServiceImpl implements MediaValidationService {

    MediaQueryService mediaQueryService;

    @Override
    public FileDto requireImage(FileDto fileDto, String fieldName) {
        if (!isImage(fileDto.getMimeType())){
            throw FileExceptions.invalidFileType(fieldName);
        }
        return fileDto;
    }

    @Override
    public List<FileDto> requireImages(List<FileDto> files, String fieldName) {
        for (FileDto fileDto : files) {
            if (!isImage(fileDto.getMimeType())){
                throw FileExceptions.invalidFileType(fieldName + "[" + fileDto.getId() + "]");
            }
        }
        return files;
    }

    @Override
    public FileDto requireMedia(UUID id, Set<String> allowedMimePrefixes, String fieldName) {
        return null;
    }
}
