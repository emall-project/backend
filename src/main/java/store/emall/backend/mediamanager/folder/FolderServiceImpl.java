package store.emall.backend.mediamanager.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.service.FileService;
import store.emall.backend.mediamanager.folder.dto.FolderDto;
import store.emall.backend.mediamanager.folder.dto.FolderFilter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final FileService fileService;
    private final FolderSpecificationBuilder folderSpecificationBuilder;
    private final FolderServiceHelper folderServiceHelper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<FolderDto> getAll(Pageable pageable, FolderFilter filter) {
        Specification<Folder> spec = folderSpecificationBuilder.build(filter);

        Page<FolderDto> folderPage = folderRepository.findAll(spec, pageable).map(FolderMapper::toDto);

        return PaginatedResponse.of(folderPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderDto> getAllFolderList(FolderFilter filter) {
        Specification<Folder> spec = folderSpecificationBuilder.build(filter);

        List<Folder> folders = (spec == null) ? folderRepository.findAll() : folderRepository.findAll(spec);

        return folders.stream().map(FolderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FolderDto getById(Long id) {
        Folder folder = folderServiceHelper.getFolder(id);

        return FolderMapper.toDto(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public FolderDto getByShopIdAndId(Long shopId, Long id) {
        Folder folder = folderServiceHelper.getFolder(shopId, id);

        return FolderMapper.toDto(folder);
    }


    @Transactional(readOnly = true)
    public FolderDto getByIdAndScope(Long id, ScopeType scope) {
        Folder folder = folderServiceHelper.getFolder(id, scope);
        return FolderMapper.toDto(folder);
    }

    @Override
    @Transactional
    public FolderDto create(FolderDto folderDto) {
        folderServiceHelper.validateScopeConsistency(folderDto);

        Folder parent = folderServiceHelper.validateAndLoadParent(folderDto.getParentId(), folderDto.getShopId(), folderDto.getScope());


        folderServiceHelper.validateUniqueName(folderDto.getParentId(), folderDto.getName());

        Folder folder = FolderMapper.toEntity(folderDto, parent);
        return FolderMapper.toDto(folderRepository.save(folder));
    }

    @Override
    @Transactional
    public FolderDto shopCreate(FolderDto folderDto) {
        if (folderDto.getParentId() == null) {
            throw FolderExceptions.rootFolderCreationNotAllowed();
        }
        return create(folderDto);
    }

    @Override
    @Transactional
    public FolderDto systemUpdate(FolderDto dto) {
        Folder existing = folderServiceHelper.getFolder(dto.getId());
        return update(existing, dto);
    }

    @Override
    @Transactional
    public FolderDto shopUpdate(FolderDto dto) {
        if (dto.getParentId() == null) {
            throw FolderExceptions.rootFolderCreationNotAllowed();
        }

        Folder existing = folderServiceHelper.getFolder(
                dto.getId(),
                dto.getShopId(),
                ScopeType.SHOP,
                ManagedByType.SHOP
        );
        return update(existing, dto);
    }

    @Transactional
    public FolderDto adminUpdate(FolderDto dto) {
        Folder existing = folderServiceHelper.getFolder(dto.getId(), ScopeType.SYSTEM, ManagedByType.ADMIN);
        return update(existing, dto);
    }

    @Override
    @Transactional
    public void systemDelete(Long id) {
        Folder folder = folderServiceHelper.getFolder(id);

        delete(folder);
    }

    @Override
    @Transactional
    public void shopDelete(Long shopId, Long id) {
        Folder folder = folderServiceHelper.getFolder(id, shopId, ScopeType.SHOP, ManagedByType.SHOP);
        folderServiceHelper.validateManagedDeleteSubtree(folder, ScopeType.SHOP, ManagedByType.SHOP, shopId);
        delete(folder);
    }

    @Transactional
    public void adminDelete(Long id, ScopeType scope, ManagedByType managedBy) {
        Folder folder = folderServiceHelper.getFolder(id, scope, managedBy);
        folderServiceHelper.validateManagedDeleteSubtree(folder, scope, managedBy, null);
        delete(folder);
    }

    private FolderDto update(Folder existing, FolderDto dto) {
        Folder parent = folderServiceHelper.validateAndLoadParent(
                dto.getParentId(),
                existing.getShopId(),
                existing.getScope()
        );

        folderServiceHelper.validateUniqueNameForUpdate(existing, dto);
        FolderMapper.merge(existing, dto, parent);
        folderServiceHelper.validateHierarchy(existing);

        return FolderMapper.toDto(folderRepository.save(existing));
    }

    private void delete(Folder folder) {
        List<Folder> innerFolders = folderRepository.findByParent_Id(folder.getId());
        for (Folder innerFolder : innerFolders) {
            delete(innerFolder);
        }

        fileService.deleteByFolderId(folder.getId());
        folderRepository.delete(folder);
    }

    @Override
    public Long getSystemFolderId(SystemFolder systemFolder) {
        Folder folder = folderRepository.findByNameAndScopeAndManagedByAndParentIsNull(
                systemFolder.getFolderName(),
                ScopeType.SYSTEM,
                ManagedByType.SYSTEM
        ).orElseThrow(FolderExceptions::folderNotFound);
        return folder.getId();
    }
}
