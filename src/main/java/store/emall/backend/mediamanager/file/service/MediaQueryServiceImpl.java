package store.emall.backend.mediamanager.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.*;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.url.MediaUrlService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MediaQueryServiceImpl implements MediaQueryService {

    private final FileRepository fileRepository;
    private final FileSpecificationBuilder fileSpecificationBuilder;
    private final FileServiceHelper fileServiceHelper;
    private final MediaUrlService mediaUrlService;

    @Value("${media.cache-control.private:private, max-age=300}")
    private String privateCacheControl;


    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<FileDto> getAll(Pageable pageable, FileFilter fileFilter) {
        validateShopScopedFilter(fileFilter);
        Specification<File> spec = fileSpecificationBuilder.build(fileFilter);

        Page<FileDto> page = fileRepository.findAll(spec, pageable)
                .map(mediaUrlService::toDtoWithUrls);

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileDto> getAllFileList(FileFilter fileFilter) {
        validateShopScopedFilter(fileFilter);
        Specification<File> spec = fileSpecificationBuilder.build(fileFilter);

        List<File> files = (spec == null) ? fileRepository.findAll() : fileRepository.findAll(spec);

        return mediaUrlService.toDtosWithUrls(files);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable("fileCache")
    public FileDto getById(UUID id) {
        if (id == null) {
            return null;
        }
        File file = fileServiceHelper.getFile(id);
        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDto getByIdAndScope(UUID id, ScopeType scopeType) {
        if (id == null) {
            return null;
        }
        File file = fileServiceHelper.getFile(id, scopeType);
        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable("fileCache")
    public FileDto getByShopIdAndId(Long shopId, UUID id) {
        if (id == null) {
            return null;
        }
        File file = fileServiceHelper.getFile(id, shopId);
        return mediaUrlService.toDtoWithUrls(file);
    }

    @Override
    @Transactional
    public List<FileDto> getByFolderId(Long id) {
        List<File> files = fileRepository.findByFolder_Id(id);
        return mediaUrlService.toDtosWithUrls(files);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return fileRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileDto> getByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<File> files = fileRepository.findAllById(ids);
        return mediaUrlService.toDtosWithUrls(files);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, FileDto> getMedia(List<UUID> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }
        List<FileDto> files = getByIds(imageIds);

        Map<UUID, FileDto> fileDtoMap = new HashMap<>();
        for (FileDto fileDto : files) {
            fileDtoMap.put(fileDto.getId(), fileDto);
        }
        return fileDtoMap;
    }

    private void validateShopScopedFilter(FileFilter fileFilter) {
        if (fileFilter == null || fileFilter.getShopId() == null || fileFilter.getFolderId() == null) {
            return;
        }

        fileServiceHelper.getFolder(fileFilter.getFolderId(), fileFilter.getShopId());
    }

}
