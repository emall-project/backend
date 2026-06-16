package store.emall.backend.catalog.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.campaigns.offer.ActiveProductDiscountDto;
import store.emall.backend.catalog.brand.Brand;
import store.emall.backend.catalog.brand.BrandRepository;
import store.emall.backend.catalog.category.Category;
import store.emall.backend.catalog.category.CategoryRepository;
import store.emall.backend.mediamanager.file.dto.FileLightDto;
import store.emall.backend.catalog.product.info.ProductInfoDto;
import store.emall.backend.catalog.product.info.ProductInfoMapper;
import store.emall.backend.catalog.product.light.ProductLightMapper;
import store.emall.backend.catalog.product.product_media.ProductMedium;
import store.emall.backend.catalog.product.summary.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.catalog.product.light.ProductLightDto;
import store.emall.backend.catalog.product.light.ProductLightRow;
import store.emall.backend.catalog.product.product_variant.*;
import store.emall.backend.catalog.tag.Tag;
import store.emall.backend.catalog.tag.TagService;
import store.emall.backend.catalog.tag.TagMapper;
import store.emall.backend.interaction.ai.product_similarity.ProductSimilarityService;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsQuery;
import store.emall.backend.interaction.ai.product_similarity.dto.SimilarProductsResult;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagService tagService;
    private final ProductVariantService productVariantService;
    private final ProductSpecificationBuilder productSpecificationBuilder;
    private final ProductServiceHelper productServiceHelper;
    private final ProductSimilarityService productSimilarityService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProductLightDto> getAllLight(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = productSpecificationBuilder.build(filter);
        Page<Long> productPage = productRepository.findIdsBySpecification(spec, pageable);

        List<Long> productIds = productPage.getContent()
                .stream()
                .toList();

        ProductLightLookup lightLookup = loadProductLightLookup(productIds, true);
        Page<ProductLightDto> dtoPage = productPage.map(productId -> toProductLightDto(productId, lightLookup, true));

        return PaginatedResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSummary getSummary(ProductFilter filter) {
        Specification<Product> spec = productSpecificationBuilder.build(filter);
        long productCount = productRepository.count(spec);
        AudienceDistribution audienceDistribution = productRepository.getAudienceDistribution(spec);
        AgeDistribution ageDistribution = productRepository.getAgeDistribution(spec);
        List<CategoryDistribution> categoryDistributions = productRepository.getCategoryDistribution(spec);
        List<BrandDistribution> brandDistributions = productRepository.getBrandDistribution(spec);
        PriceRange priceRange = productRepository.getPriceRange(spec);
        List<AttributeSummary> attributeSummary = productRepository.getAttributeSummary(spec);

        return new ProductSummary(
                productCount,
                audienceDistribution,
                ageDistribution,
                categoryDistributions,
                brandDistributions,
                priceRange,
                attributeSummary
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLightDto> getAllProductList(ProductFilter filter) {
        Specification<Product> spec = productSpecificationBuilder.build(filter);
        List<Long> productIds = productRepository.findIdsBySpecification(spec);

        ProductLightLookup lightLookup = loadProductLightLookup(productIds, false);
        return productIds.stream()
                .map(productId -> toProductLightDto(productId, lightLookup, false))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLightDto> getLightByIds(List<Long> productIds) {
        return loadActiveProductLightDtos(productIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLightDto> getRandomLight(Integer limit) {
        int safeLimit = limit == null || limit < 1 ? 10 : Math.min(limit, 50);
        List<Long> productIds = productRepository.findRandomActiveProductIds(PageRequest.of(0, safeLimit));

        ProductLightLookup lightLookup = loadProductLightLookup(productIds, true);
        return toActiveProductLightDtos(productIds, lightLookup, true);
    }

    private ProductLightLookup loadProductLightLookup(List<Long> productIds, boolean includeDiscounts) {
        if (productIds == null || productIds.isEmpty()) {
            return ProductLightLookup.empty();
        }

        List<ProductLightRow> rows = productRepository.findLightRowsByProductIds(productIds);

        Map<Long, ProductLightRow> rowMap = new HashMap<>();
        List<UUID> mediaIds = new ArrayList<>();

        for (ProductLightRow row : rows) {
            rowMap.put(row.getProductId(), row);
            if (row.getMediumId() != null) {
                mediaIds.add(row.getMediumId());
            }
        }

        Map<UUID, FileLightDto> mediaMap = productServiceHelper.getMedia(mediaIds);
        Map<Long, ActiveProductDiscountDto> discountMap = includeDiscounts
                ? productServiceHelper.getActiveDiscounts(productIds)
                : Collections.emptyMap();

        return new ProductLightLookup(rowMap, mediaMap, discountMap);
    }

    private ProductLightDto toProductLightDto(
            Long productId,
            ProductLightLookup lightLookup,
            boolean includeDiscounts
    ) {
        ProductLightDto dto = ProductLightMapper.toProductLightDto(
                productId,
                lightLookup.rowMap(),
                lightLookup.mediaMap()
        );

        if (!includeDiscounts) {
            return dto;
        }

        return productServiceHelper.injectLightDiscount(dto, lightLookup.discountMap());
    }

    private List<ProductLightDto> toActiveProductLightDtos(
            List<Long> productIds,
            ProductLightLookup lightLookup,
            boolean includeDiscounts
    ) {
        return productIds.stream()
                .map(productId -> toProductLightDto(productId, lightLookup, includeDiscounts))
                .filter(dto -> dto.getName() != null)
                .filter(dto -> Boolean.TRUE.equals(dto.getIsActive()))
                .toList();
    }

    private List<Long> sanitizeProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return productIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private record ProductLightLookup(
            Map<Long, ProductLightRow> rowMap,
            Map<UUID, FileLightDto> mediaMap,
            Map<Long, ActiveProductDiscountDto> discountMap
    ) {
        private static ProductLightLookup empty() {
            return new ProductLightLookup(
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getById(Long id, Boolean activeOnly) {
        Product product = activeOnly ? productRepository.findByIdAndIsActive(id, true)
                .orElseThrow(ProductExceptions::productNotFound) :
                productRepository.findById(id)
                        .orElseThrow(ProductExceptions::productNotFound);

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    public ProductDto getByShopIdAndId(Long shopId, Long id) {
        Product product = productRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(ProductExceptions::productNotFound);

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(ProductExceptions::productNotFound);

        if (product.getIsActive().equals(Boolean.FALSE)) {
            throw ProductExceptions.productNotActive();
        }

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getByShopIdAndSlug(Long shopId, String slug) {
        log.info("shopId : {}, slug : {}", shopId, slug);
        Product product = productRepository.findByShopIdAndSlug(shopId, slug)
                .orElseThrow(ProductExceptions::productNotFound);

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInfoDto getProductInfo(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);

        if (product.getIsActive().equals(Boolean.FALSE)) {
            throw ProductExceptions.productNotActive();
        }

        return ProductInfoMapper.toInfoDto(product, getDefaultMedium(product));
    }

    private FileLightDto getDefaultMedium(Product product) {
        UUID mediumId = getDefaultMediumId(product);
        if (mediumId == null) {
            return null;
        }

        return productServiceHelper.getMedia(List.of(mediumId)).get(mediumId);
    }

    private UUID getDefaultMediumId(Product product) {
        if (product == null || product.getDefaultVariant() == null || product.getDefaultVariant().getMedia() == null) {
            return null;
        }

        return product.getDefaultVariant().getMedia().stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparing(medium -> Optional
                        .ofNullable(medium.getSortOrder())
                        .orElse(Integer.MAX_VALUE)))
                .map(ProductMedium::getMediumId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLightDto> getSimilar(Long id, Integer topK) {
        Product product = productRepository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                .orElseThrow(ProductExceptions::productNotFound);

        SimilarProductsQuery query = SimilarProductsQuery.builder()
                .productId(id)
                .activeOnly(Boolean.TRUE)
                .sameMallOnly(Boolean.TRUE)
                .topK(topK)
                .build();
        SimilarProductsResult result = productSimilarityService.getSimilarProducts(query);
        if (result.isSuccess() == false) {
            log.warn("Could not fetch similar products from interaction productId={}. Using catalog fallback.", id);
            return getFallbackSimilarProducts(product, topK);
        }
        SimilarProductsResult similarProductsResult = result;
        List<Long> productIds = sanitizeProductIds(similarProductsResult.getProductIds());
        ProductLightLookup lightLookup = loadProductLightLookup(productIds, true);
        return toActiveProductLightDtos(productIds, lightLookup, true);


    }

    private List<ProductLightDto> getFallbackSimilarProducts(Product product, Integer topK) {
        int limit = topK == null || topK < 1 ? 8 : topK;
        List<Long> productIds = productRepository.findFallbackSimilarProducts(
                        product.getId(),
                        product.getMallId(),
                        product.getCategory().getId(),
                        product.getBrand().getId(),
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(Product::getId)
                .toList();

        ProductLightLookup lightLookup = loadProductLightLookup(productIds, true);
        return toActiveProductLightDtos(productIds, lightLookup, true);
    }

    private List<ProductLightDto> loadActiveProductLightDtos(List<Long> productIds) {
        List<Long> sanitizedIds = sanitizeProductIds(productIds);
        if (sanitizedIds.isEmpty()) {
            return List.of();
        }

        ProductLightLookup lightLookup = loadProductLightLookup(sanitizedIds, true);
        return toActiveProductLightDtos(sanitizedIds, lightLookup, true);
    }

    @Override
    public ProductDto create(Long mallId, Long shopId, ProductDto dto) {

        // validation
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        productServiceHelper.audienceValidation(dto, category, brand);

        if (productServiceHelper.slugExistsInTheSameStore(dto.getSlug(), shopId)) {
            throw ProductExceptions.slugExistsInTheSameStore();
        }

        productServiceHelper.validateSingleDefaultVariant(dto);
        if (dto.getVariants().size() > 1) {
            productServiceHelper.validateVariantsHaveAttributes(dto.getVariants());
        }

        List<Tag> tags = null;
        if (dto.getTags() != null) {
            tags = tagService.resolveTags(dto.getTags())
                    .stream().map(TagMapper::toEntity).toList();
        }

        Product product = ProductMapper.toEntity(dto, category, brand, tags);

        product.setMallId(mallId);
        product.setShopId(shopId);

        Product saved = productRepository.save(product);

        List<ProductVariantDto> variantDtos = new ArrayList<>();
        ProductVariant defaultVariant = null;
        for (ProductVariantDto v : dto.getVariants()) {
            ProductVariantDto savedVariant = productVariantService.create(saved.getId(), v);
            variantDtos.add(savedVariant);
            if (savedVariant.getIsDefault()) {
                defaultVariant = ProductVariantMapper.toEntity(savedVariant, product);
            }
        }
        saved.setDefaultVariant(defaultVariant);
        ProductDto result = ProductMapper.toDto(productRepository.save(saved));

        result.setVariants(variantDtos);
        productServiceHelper.publishCreatedJob(product);
        return result;
    }

    @Override
    // update only product basic info
    public ProductDto update(Long mallId, Long shopId, ProductDto dto) {

        Product existing = productRepository.findById(dto.getId())
                .orElseThrow(ProductExceptions::productNotFound);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        productServiceHelper.audienceValidation(dto, category, brand);

        if (!existing.getSlug().equals(dto.getSlug()) &&
                productServiceHelper.slugExistsInTheSameStore(dto.getSlug(), existing.getShopId())) {
            log.warn("Slug {} already exists", dto.getSlug());
            throw ProductExceptions.slugExistsInTheSameStore();
        }

        if (!existing.getMallId().equals(mallId)) {
            throw ProductExceptions.productDoesNotBelongToMall();
        }

        if (!existing.getShopId().equals(shopId)) {
            throw ProductExceptions.productDoesNotBelongToStore();
        }

        // Resolve tags

        List<Tag> tags = new ArrayList<>();
        if (dto.getTags() != null) {
            tags = tagService.resolveTags(dto.getTags())
                    .stream()
                    .map(TagMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new));
        }


        if (Boolean.TRUE.equals(existing.getIsActive()) && Boolean.FALSE.equals(dto.getIsActive())) {
            deactivation(existing);
        }
        if (Boolean.FALSE.equals(existing.getIsActive()) && Boolean.TRUE.equals(dto.getIsActive())) {
            activation(existing);
        }
        // Update basic fields
        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setIsActive(dto.getIsActive());
        existing.setShortDescription(dto.getShortDescription());
        existing.setDescription(dto.getDescription());
        existing.setCategory(category);
        existing.setBrand(brand);

        existing.getTags().clear();
        existing.getTags().addAll(tags);

        Product savedProduct = productRepository.save(existing);

        productServiceHelper.publishUpdatedJob(savedProduct);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public void delete(Long shopId, Long id) {
        Product product = productRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(ProductExceptions::productNotFound);

        // todo check if there any order, discount, variant
        productServiceHelper.publishDeletedJob(product.getId());
        productRepository.delete(product);
    }

    public void activation(Product product) {
    }

    public void deactivation(Product product) {
    }

}
