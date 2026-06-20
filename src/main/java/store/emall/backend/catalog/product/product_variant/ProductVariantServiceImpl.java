package store.emall.backend.catalog.product.product_variant;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.catalog.attribute.Attribute;
import store.emall.backend.catalog.attribute.AttributeExceptions;
import store.emall.backend.catalog.attribute.AttributeRepository;
import store.emall.backend.catalog.attribute.attribute_options.AttributeOption;
import store.emall.backend.catalog.attribute.attribute_options.AttributeOptionRepository;
import store.emall.backend.catalog.attribute.attribute_options.AttributeOptionsExceptions;
import store.emall.backend.catalog.product.Product;
import store.emall.backend.catalog.product.ProductExceptions;
import store.emall.backend.catalog.product.ProductRepository;
import store.emall.backend.catalog.product.ProductServiceHelper;
import store.emall.backend.catalog.product.product_media.ProductMediumMapper;
import store.emall.backend.catalog.product.product_media.ProductMediumDto;
import store.emall.backend.catalog.product.product_variant.variant_attribute.VariantAttributeDto;
import store.emall.backend.common.EntityType;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final FileService fileService;
    private final ProductServiceHelper productServiceHelper;
    private final MediaVisibilityService mediaVisibilityService;

    @Override
    public ProductVariantDto add(Long shopId, Long productId, ProductVariantDto dto) {
        // Fetch product or throw
        Product product = productRepository.findByShopIdAndId(shopId, productId)
                .orElseThrow(ProductExceptions::productNotFound);

        validateMedia(dto.getMedia());

        // Map DTO to entity
        ProductVariant variant = ProductVariantMapper.toEntity(dto, product);

        // Add Media
        loadAndValidateMedia(dto, variant);

        // Add attributes, checking duplicates
        loadAndValidateAttribute(dto, variant);

        // Save the variant
        ProductVariant saved = productVariantRepository.saveAndFlush(variant);

        productServiceHelper.syncVariantMediaBindings(saved);

        if (saved.getIsDefault()) {
            productVariantRepository.clearDefaultForProduct(variant.getProduct().getId());
            productRepository.updateDefaultVariant(variant.getProduct().getId(), variant.getId());
        }
        // Convert to DTO and inject media
        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return productServiceHelper.injectMedium(savedDto);
    }

    @Override
    public ProductVariantDto create(Long productId, ProductVariantDto dto) {
        // Fetch product or throw
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductExceptions::productNotFound);

        validateMedia(dto.getMedia());

        // Map DTO to entity
        ProductVariant variant = ProductVariantMapper.toEntity(dto, product);

        // Add Media
        loadAndValidateMedia(dto, variant);

        // Add attributes, checking duplicates
        loadAndValidateAttribute(dto, variant);

        // Save the variant
        ProductVariant saved = productVariantRepository.saveAndFlush(variant);

        productServiceHelper.syncVariantMediaBindings(saved);

        // Convert to DTO and inject media
        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return productServiceHelper.injectMedium(savedDto);
    }

    @Override
    public ProductVariantDto update(Long shopId, Long productId, ProductVariantDto dto) {

        ProductVariant existing = productVariantRepository.findByShopIdAndProductIdAndId(shopId, productId, dto.getId()).orElseThrow(
                ProductVariantExceptions::variantNotFound
        );

        validateMedia(dto.getMedia());

        // basic field
        existing.setName(dto.getName());
        existing.setBasePrice(dto.getBasePrice());

        // Add Media
        existing.getMedia().clear();
        loadAndValidateMedia(dto, existing);

        existing.getVariantAttributes().clear();
        loadAndValidateAttribute(dto, existing);

        // default variant
        loadAndUpdateDefaultVariant(dto, existing);

        // Save the variant
        ProductVariant saved = productVariantRepository.save(existing);

        productServiceHelper.syncVariantMediaBindings(saved);
        
        // Convert to DTO and inject media
        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return productServiceHelper.injectMedium(savedDto);
    }


    @Override
    public void delete(Long shopId, Long productId, Long id) {
        ProductVariant variant = productVariantRepository.findByShopIdAndProductIdAndId(shopId, productId, id).orElseThrow(
                ProductVariantExceptions::variantNotFound
        );
        if (variant.getIsDefault().equals(Boolean.TRUE)) {
            throw ProductVariantExceptions.defaultVariantDeletionNotAllowed();
        }
        mediaVisibilityService.removeBindings(EntityType.PRODUCT_VARIANT, id, "media");
        // make sure no orders on this
        productVariantRepository.delete(variant);
    }


    private boolean validMediumType(String mimeType) {
        return mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"));
    }

    private void validateMedia(List<ProductMediumDto> media) {
        // Validate media limit
        if (media == null || media.isEmpty()) {
            throw ProductVariantExceptions.atLeastOneMediaRequired();
        } else if (media.size() > 10) {
            throw ProductVariantExceptions.mediaLimitExceeded();
        }

        // Validate media orders & existence
        Set<Integer> orders = new HashSet<>();
        media.forEach(medium -> {
            if (!orders.add(medium.getSortOrder())) {
                throw ProductVariantExceptions.duplicateMediumSort();
            }
            FileDto fileDto = fileService.getById(medium.getMediumId());

            if (!validMediumType(fileDto.getMimeType())) {
                throw ProductVariantExceptions.mediumTypeInvalid();
            }

        });
    }

    private void loadAndValidateAttribute(ProductVariantDto dto, ProductVariant variant) {
        // Add attributes, checking duplicates
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            Set<Long> attributeIds = new HashSet<>();
            for (VariantAttributeDto va : dto.getAttributes()) {

                Attribute attribute = attributeRepository.findById(va.getAttributeId())
                        .orElseThrow(AttributeExceptions::attributeNotFound);

                // Check for duplicate attributes
                if (!attributeIds.add(attribute.getId())) {
                    throw ProductVariantExceptions.duplicateAttribute();
                }

                AttributeOption option = attributeOptionRepository.findByAttribute_IdAndId(va.getAttributeId(), va.getOptionId())
                        .orElseThrow(AttributeOptionsExceptions::optionNotFound);

                variant.addVariantAttribute(attribute, option);
            }
        }
    }

    private void loadAndValidateMedia(ProductVariantDto dto, ProductVariant variant) {
        dto.getMedia().stream()
                .map(ProductMediumMapper::toEntity)
                .forEach(variant::addMedium);
    }

    private void loadAndUpdateDefaultVariant(ProductVariantDto dto, ProductVariant variant) {
        if (dto.getIsDefault().equals(variant.getIsDefault())) return;

        if (dto.getIsDefault().equals(Boolean.FALSE)) {
            throw ProductExceptions.defaultVariantRequired();
        }

        productVariantRepository.clearDefaultForProduct(variant.getProduct().getId());
        productRepository.updateDefaultVariant(variant.getProduct().getId(), variant.getId());

        variant.setIsDefault(dto.getIsDefault());

    }
}
