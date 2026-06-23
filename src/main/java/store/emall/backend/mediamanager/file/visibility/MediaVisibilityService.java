package store.emall.backend.mediamanager.file.visibility;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.EntityType;
import store.emall.backend.mediamanager.file.File;
import store.emall.backend.mediamanager.file.FileRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaVisibilityService {

    private final FileRepository fileRepository;
    private final FileBindingRepository fileBindingRepository;
    private final MediaObjectPromotionService mediaObjectPromotionService;

    @Transactional
    public void bindPublic(UUID fileId, EntityType entityType, Object entityId, String fieldName) {
        upsertBinding(fileId, entityType, entityId, fieldName, MediaVisibility.PUBLIC);
    }

    @Transactional
    public void bindPrivate(UUID fileId, EntityType entityType, Object entityId, String fieldName) {
        upsertBinding(fileId, entityType, entityId, fieldName, MediaVisibility.PRIVATE);
    }

    @Transactional
    public void syncPublicBindings(EntityType entityType, Object entityId, String fieldName, Collection<UUID> fileIds) {
        syncBindings(entityType, entityId, fieldName, fileIds, MediaVisibility.PUBLIC);
    }

    @Transactional
    public void syncPrivateBindings(EntityType entityType, Object entityId, String fieldName, Collection<UUID> fileIds) {
        syncBindings(entityType, entityId, fieldName, fileIds, MediaVisibility.PRIVATE);
    }

    @Transactional
    public void removeBindings(EntityType entityType, Object entityId, String fieldName) {
        if (entityId == null) {
            return;
        }
        List<FileBinding> bindings = fileBindingRepository.findByEntityTypeAndEntityIdAndFieldName(
                entityType,
                entityId.toString(),
                fieldName
        );
        Set<UUID> touchedFileIds = new LinkedHashSet<>();
        bindings.forEach(binding -> touchedFileIds.add(binding.getFile().getId()));
        fileBindingRepository.deleteAll(bindings);
        touchedFileIds.forEach(this::recomputeVisibility);
    }

    @Transactional
    public void removeEntityBindings(EntityType entityType, Object entityId) {
        if (entityId == null) {
            return;
        }
        List<FileBinding> bindings = fileBindingRepository.findByEntityTypeAndEntityId(entityType, entityId.toString());
        Set<UUID> touchedFileIds = new LinkedHashSet<>();
        bindings.forEach(binding -> touchedFileIds.add(binding.getFile().getId()));
        fileBindingRepository.deleteAll(bindings);
        touchedFileIds.forEach(this::recomputeVisibility);
    }

    private void syncBindings(
            EntityType entityType,
            Object entityId,
            String fieldName,
            Collection<UUID> fileIds,
            MediaVisibility visibility
    ) {
        if (entityId == null) {
            return;
        }

        Set<UUID> requestedFileIds = fileIds == null
                ? Set.of()
                : fileIds.stream().filter(Objects::nonNull).collect(LinkedHashSet::new, Set::add, Set::addAll);

        List<FileBinding> existing = fileBindingRepository.findByEntityTypeAndEntityIdAndFieldName(
                entityType,
                entityId.toString(),
                fieldName
        );

        Set<UUID> touchedFileIds = new LinkedHashSet<>();
        for (FileBinding binding : existing) {
            UUID existingFileId = binding.getFile().getId();
            if (!requestedFileIds.contains(existingFileId)) {
                fileBindingRepository.delete(binding);
                touchedFileIds.add(existingFileId);
            }
        }

        for (UUID fileId : requestedFileIds) {
            upsertBinding(fileId, entityType, entityId, fieldName, visibility);
            touchedFileIds.add(fileId);
        }

        touchedFileIds.forEach(this::recomputeVisibility);
    }

    private void upsertBinding(
            UUID fileId,
            EntityType entityType,
            Object entityId,
            String fieldName,
            MediaVisibility visibility
    ) {
        if (fileId == null || entityId == null) {
            return;
        }

        File file = fileRepository.findById(fileId).orElse(null);
        if (file == null) {
            return;
        }

        FileBinding binding = fileBindingRepository
                .findByFile_IdAndEntityTypeAndEntityIdAndFieldName(fileId, entityType, entityId.toString(), fieldName)
                .orElseGet(() -> FileBinding.builder()
                        .file(file)
                        .entityType(entityType)
                        .entityId(entityId.toString())
                        .fieldName(fieldName)
                        .build());

        binding.setVisibility(visibility);
        fileBindingRepository.save(binding);
        recomputeVisibility(fileId);
    }

    public List<FileBindingDto> getFileBindings(UUID fileId) {
        List<FileBinding> fileBindings = fileBindingRepository.findByFile_id(fileId);
        return fileBindings.stream()
                .map(FileBindingMapper::toDto)
                .toList();
    }

    @Transactional
    public void recomputeVisibility(UUID fileId) {
        File file = fileRepository.findById(fileId).orElse(null);
        if (file == null) {
            return;
        }

        boolean hasPublicBinding = fileBindingRepository.countByFile_IdAndVisibility(fileId, MediaVisibility.PUBLIC) > 0;
        if (hasPublicBinding) {
            if (!MediaVisibility.PUBLIC.equals(file.getVisibility())) {
                mediaObjectPromotionService.promote(file);
            }
            return;
        }

        if (!MediaVisibility.PRIVATE.equals(file.getVisibility())) {
            mediaObjectPromotionService.demote(file);
        }
    }
}
