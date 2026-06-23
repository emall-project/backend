package store.emall.backend.catalog.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.catalog.category.audience_config.CategoryAudienceConfig;
import store.emall.backend.catalog.category.audience_config.CategoryAudienceConfigDto;
import store.emall.backend.catalog.category.audience_config.CategoryAudienceConfigMapper;
import store.emall.backend.common.EntityType;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.catalog.product.ProductRepository;
import store.emall.backend.mediamanager.file.service.FileService;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryServiceHelper categoryServiceHelper;
    private final CategorySpecificationBuilder specificationBuilder;
    private final MediaVisibilityService mediaVisibilityService;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CategoryDto> getAll(CategoryFilter categoryFilter, Pageable pageable) {
        Specification<Category> spec = specificationBuilder.build(categoryFilter);
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

        List<UUID> imageIds = categoryPage.getContent().stream()
                .map(Category::getImageId)
                .toList();

        Map<UUID, FileDto> imagesMap = fileService.getMedia(imageIds);

        Page<CategoryDto> page = categoryPage.map(category -> {
            FileDto image = imagesMap.get(category.getImageId());

            return CategoryMapper.toDto(category, image, Collections.emptySet());
        });
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CategoryLightDto> getAllLight(CategoryFilter categoryFilter, Pageable pageable) {
        Specification<Category> spec = specificationBuilder.build(categoryFilter);

        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

        List<UUID> imageIds = categoryPage.getContent().stream()
                .map(Category::getImageId)
                .toList();

        Map<UUID, FileDto> imagesMap = fileService.getMedia(imageIds);

        Page<CategoryLightDto> page = categoryPage.map(category -> {
            FileDto image = imagesMap.get(category.getImageId());

            return CategoryMapper.toLightDto(category, image);
        });

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoryList(CategoryFilter categoryFilter) {
        Specification<Category> spec = specificationBuilder.build(categoryFilter);

        List<Category> categories = (spec == null)
                ? categoryRepository.findAll()
                : categoryRepository.findAll(spec);

        List<UUID> imageIds = categories.stream()
                .map(Category::getImageId)
                .toList();

        Map<UUID, FileDto> imagesMap = fileService.getMedia(imageIds);

        List<CategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            FileDto image = imagesMap.get(category.getImageId());

            CategoryDto dto = CategoryMapper.toDto(category, image, Collections.emptySet());
            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeDto> getTree(Boolean isActive) {

        List<Category> categories = categoryRepository
                .findByDepthLevelInAndIsActiveOrderByDepthLevelAsc(List.of(0, 1, 2), isActive);

        List<UUID> imageIds = categories.stream()
                .map(Category::getImageId)
                .toList();

        Map<UUID, FileDto> imagesMap = fileService.getMedia(imageIds);


        Map<Long, CategoryTreeDto> dtoMap = new HashMap<>();
        List<CategoryTreeDto> roots = new ArrayList<>();

        for (Category category : categories) {
            FileDto fileDto = imagesMap.get(category.getImageId());

            CategoryTreeDto dto = CategoryMapper.toTreeDto(category, fileDto);
            dtoMap.put(dto.getId(), dto);
        }

        for (Category category : categories) {
            CategoryTreeDto current = dtoMap.get(category.getId());

            if (category.getParent() == null) {
                roots.add(current);
                continue;
            }

            CategoryTreeDto parentDto = dtoMap.get(category.getParent().getId());

            if (parentDto != null) {
                parentDto.getChildren().add(current);
            }
        }

        return roots;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return withImages(CategoryMapper.toDto(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getActiveById(Long id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return withImages(CategoryMapper.toDto(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return withImages(CategoryMapper.toDto(category));
    }

    @Override
    public CategoryDto getActiveBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return withImages(CategoryMapper.toDto(category));
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        if (slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }

        // validation
        categoryServiceHelper.validateCategoryParent(dto);
        categoryServiceHelper.validateAudienceConfigAllowed(dto);
        categoryServiceHelper.validateAudienceConfig(dto);

        FileDto categoryImage = fileService.getAndValidateImage(dto.getImageId(), "CategoryImage");
        categoryServiceHelper.validateAudienceConfigImages(dto.getAudienceConfig());


        // set depth level
        if (dto.getParentId() == null) {
            dto.setDepthLevel(0);
        } else {
            Category parent = categoryRepository.findById(dto.getParentId()).orElseThrow(CategoryExceptions::categoryNotFound);
            dto.setDepthLevel(parent.getDepthLevel() + 1);
        }
        Category category = CategoryMapper.toEntity(dto);

        Category saved = categoryRepository.save(category);
        categoryServiceHelper.syncCategoryMediaBindings(saved);

        return categoryServiceHelper.injectImages(CategoryMapper.toDto(saved, categoryImage));
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        Category existing = categoryRepository.findById(dto.getId())
                .orElseThrow(CategoryExceptions::categoryNotFound);

        if (!existing.getSlug().equals(dto.getSlug()) && slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }

        categoryServiceHelper.validateCategoryParent(dto);
        categoryServiceHelper.validateAudienceConfigAllowed(dto);
        categoryServiceHelper.validateAudienceConfig(dto);
//  TODO : consider the existing config in validation
        FileDto categoryImage = fileService.getAndValidateImage(dto.getImageId(), "CategoryImage");
        categoryServiceHelper.validateAudienceConfigImages(dto.getAudienceConfig());

        if (Boolean.TRUE.equals(existing.getIsActive()) && Boolean.FALSE.equals(dto.getIsActive())) {
            categoryServiceHelper.deactivation(existing);
        }
        if (Boolean.FALSE.equals(existing.getIsActive()) && Boolean.TRUE.equals(dto.getIsActive())) {
            categoryServiceHelper.activation(existing);
        }

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setImageId(dto.getImageId());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setIsActive(dto.getIsActive());
        existing.setParent(dto.getParentId() != null
                ? Category.builder().id(dto.getParentId()).build()
                : null);

        categoryServiceHelper.syncAudienceConfigs(existing, dto.getAudienceConfig());


        // set depth level
        if (dto.getParentId() == null) {
            dto.setDepthLevel(0);
        } else {
            Category parent = categoryRepository.findById(dto.getParentId()).orElseThrow(CategoryExceptions::categoryNotFound);
            dto.setDepthLevel(parent.getDepthLevel() + 1);
        }

        Category saved = categoryRepository.save(existing);
        categoryServiceHelper.syncCategoryMediaBindings(saved);
        return categoryServiceHelper.injectImages(CategoryMapper.toDto(saved, categoryImage));
    }

    @Override
    public CategoryDto addAudienceConfig(Long categoryId, CategoryAudienceConfigDto categoryAudienceConfigDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        CategoryDto categoryDto = CategoryMapper.toDto(category);
        categoryDto.getAudienceConfig().add(categoryAudienceConfigDto);

        if (!categoryServiceHelper.isAudienceConfigAllowed(categoryDto)) {
            throw CategoryExceptions.audienceConfigNotAllowed();
        }
        categoryServiceHelper.validateAudienceConfig(categoryDto);
        fileService.getAndValidateImage(categoryAudienceConfigDto.getImageId(), "CategoryAudienceImage");

        CategoryAudienceConfig config = CategoryAudienceConfigMapper.toEntity(categoryAudienceConfigDto, category);
        category.getAudienceConfig().add(config);
        Category saved = categoryRepository.save(category);
        categoryServiceHelper.syncCategoryMediaBindings(saved);

        return withImages(CategoryMapper.toDto(saved));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        long childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            throw CategoryExceptions.categoryHasChildren();
        }
        long productCount = productRepository.countByCategory_Id(id);
        if (productCount > 0) {
            throw CategoryExceptions.categoryHasProducts();
        }
        mediaVisibilityService.removeEntityBindings(EntityType.CATEGORY, id);
        categoryRepository.delete(category);
    }

    @Override
    public void removeAudienceConfig(Long categoryId, Long id) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryExceptions::categoryNotFound);
        boolean removed = category.getAudienceConfig().removeIf(config -> config.getId().equals(id));

        if (!removed) {
            throw CategoryExceptions.categoryAudienceConfigNotFound();
        }
        categoryRepository.save(category);
        categoryServiceHelper.syncCategoryMediaBindings(category);
    }

    @Override
    public boolean slugExists(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    private CategoryDto withImages(CategoryDto dto) {
        return withAudienceConfigImages(categoryServiceHelper.injectImageUrl(dto));
    }

    private CategoryDto withAudienceConfigImages(CategoryDto dto) {
        Set<CategoryAudienceConfigDto> configs = dto.getAudienceConfig();
        if (configs == null || configs.isEmpty()) {
            return dto;
        }

        List<UUID> imageIds = configs.stream()
                .map(CategoryAudienceConfigDto::getImageId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (imageIds.isEmpty()) {
            return dto;
        }

        Map<UUID, FileDto> imagesMap = fileService.getMedia(imageIds);
        if (imagesMap == null || imagesMap.isEmpty()) {
            return dto;
        }

        configs.forEach(config -> config.setImage(imagesMap.get(config.getImageId())));
        return dto;
    }

}
