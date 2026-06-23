package store.emall.backend.mediamanager.file.service;

import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MediaValidationService {

    FileDto requireImage(FileDto fileDto, String fieldName);

    List<FileDto> requireImages(List<FileDto> fileDtos, String fieldName);

    FileDto requireMedia(UUID id, Set<String> allowedMimePrefixes, String fieldName);
}
