package store.emall.backend.catalog.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import store.emall.backend.campaigns.offer.*;
import store.emall.backend.catalog.brand.Brand;
import store.emall.backend.catalog.category.Category;
import store.emall.backend.catalog.product.product_variant.ProductVariant;
import store.emall.backend.common.EntityType;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.catalog.job.ProductJob;
import store.emall.backend.catalog.product.light.ProductLightDto;
import store.emall.backend.catalog.product.product_media.ProductMediumDto;
import store.emall.backend.catalog.product.product_variant.ProductVariantDto;
import store.emall.backend.catalog.publisher.JobPublisher;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceHelper {
    private final ProductRepository productRepository;
    //    private final CampaignsClient campaignsClient;
    private final ObjectProvider<OfferService> offerServiceProvider;
    private final FileService fileService;
    private final JobPublisher jobPublisher;
    private final MediaVisibilityService mediaVisibilityService;

    boolean slugExistsInTheSameShop(String slug, Long shopId) {
        boolean result = productRepository.existsBySlugIgnoreCaseAndShopId(slug, shopId);
        return result;
    }

    public static void validateSingleDefaultVariant(ProductDto dto) {
        long defaults =
                dto.getVariants().stream()
                        .filter(ProductVariantDto::getIsDefault)
                        .count();

        if (defaults > 1)
            throw ProductExceptions.multipleDefaultVariants();
        if (defaults == 0) {
            throw ProductExceptions.defaultVariantRequired();
        }
    }

    public static void validateVariantsHaveAttributes(List<ProductVariantDto> variants) {
        for (ProductVariantDto variant : variants) {
            if (variant.getAttributes() == null || variant.getAttributes().isEmpty()) {
                throw ProductExceptions.variantShouldHasAttribute();
            }
        }
    }

    public ProductDto injectDiscount(ProductDto dto) {
        log.info("Inject discount");
        if (dto == null || dto.getVariants() == null || dto.getVariants().isEmpty()) {
            return dto;
        }
        // TODO : VALIDATE this, i use different service method cause i didn't find the used one
        ActiveProductDiscountDto offer = offerService().getActiveDiscountForProduct(dto.getId());

//        Map<Long, ActiveOfferDto.VariantDiscountDto> priceMap = offer.getVariantPrices()
//                .stream()
//                .collect(Collectors.toMap(
//                        ActiveOfferDto.VariantDiscountDto::getVariantId,
//                        vp -> vp
//                ));
//
//        dto.getVariants().forEach(variant -> {
//            ActiveOfferDto.VariantDiscountDto discount = priceMap.get(variant.getId());
//            if (discount != null) {
//                variant.setHasDiscount(true);
//                variant.setDiscountedPrice(discount.getDiscountedPrice());
//                variant.setDiscountType(discount.getDiscountType());
//                variant.setDiscountValue(discount.getDiscountValue());
//                variant.setOfferId(offer.getOfferId());
//            } else {
//                variant.setHasDiscount(false);
//            }
//        });
        return dto;
    }


    public ProductDto injectMedium(ProductDto dto) {
        for (ProductVariantDto v : dto.getVariants()) {
            injectMedium(v);
        }
        return dto;
    }


    public ProductVariantDto injectMedium(ProductVariantDto dto) {
        if (dto.getMedia() == null || dto.getMedia().isEmpty()) {
            return dto;
        }

        for (ProductMediumDto medium : dto.getMedia()) {
            FileDto file = fileService.getById(medium.getMediumId());

            //inject medium File
            medium.setMediumFile(file);

        }

        return dto;
    }

    public Map<UUID, FileDto> getMedia(List<UUID> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<FileDto> files = fileService.getByIds(mediaIds);

        Map<UUID, FileDto> fileDtoMap = new HashMap<>();
        for (FileDto fileDto : files) {
            fileDto.setId(fileDto.getId());
            fileDto.setOriginalFileUrl(fileDto.getOriginalFileUrl());
            fileDto.setMediumFileUrl(fileDto.getMediumFileUrl());
            fileDto.setSmallFileUrl(fileDto.getSmallFileUrl());
            fileDtoMap.put(fileDto.getId(), fileDto);
        }
        return fileDtoMap;

    }

    public void syncVariantMediaBindings(ProductVariant variant) {
        List<UUID> mediaIds = variant.getMedia() == null
                ? List.of()
                : variant.getMedia().stream()
                .map(medium -> medium.getMediumId())
                .toList();

        mediaVisibilityService.syncPublicBindings(
                EntityType.PRODUCT_VARIANT,
                variant.getId(),
                "media",
                mediaIds
        );
    }

    public void publishCreatedJob(Product product) {
        ProductJob productJob = ProductMapper.toProductJob(product);
        jobPublisher.publishProductCreatedJob(productJob);
    }

    public void publishUpdatedJob(Product product) {
        ProductJob productJob = ProductMapper.toProductJob(product);
        jobPublisher.publishProductUpdatedJob(productJob);
    }

    public void publishDeletedJob(Long productId) {
        jobPublisher.publishProductDeletedJob(productId);
    }

    public void validateTargetedAudience(TargetedAudience productTargetedAudience, TargetedAudience categoryTargetedAudience) {
        if (categoryTargetedAudience == TargetedAudience.ALL) return;
        if (productTargetedAudience == categoryTargetedAudience) return;
        throw ProductExceptions.invalidProductAudienceForCategory();
    }

    public void validateAgeGroup(AgeGroup productAgeGroup, AgeGroup categoryAgeGroup) {
        if (categoryAgeGroup == AgeGroup.ALL) return;
        if (productAgeGroup == categoryAgeGroup) return;
        throw ProductExceptions.invalidProductAgeGroupForCategory();
    }

    public void audienceValidation(ProductDto dto, Category category, Brand brand) {

        validateTargetedAudience(dto.getTargetedAudience(), category.getTargetedAudience());
        validateAgeGroup(dto.getAgeGroup(), category.getAgeGroup());

        validateTargetedAudience(dto.getTargetedAudience(), brand.getTargetedAudience());
        validateAgeGroup(dto.getAgeGroup(), brand.getAgeGroup());
    }

    public Map<Long, ActiveProductDiscountDto> getActiveDiscounts(List<Long> productIds) {
        log.info("Getting active discount for productIds={}", productIds);
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> sanitizedIds = productIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (sanitizedIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ActiveProductDiscountDto> offers = offerService().getActiveDiscountsForProducts(
                sanitizedIds
        );

        return offers.stream()
                .filter(Objects::nonNull)
                .filter(dto -> dto.getProductId() != null)
                .collect(Collectors.toMap(
                        ActiveProductDiscountDto::getProductId,
                        Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        HashMap::new
                ));

    }

    public ProductLightDto injectLightDiscount(
            ProductLightDto dto,
            Map<Long, ActiveProductDiscountDto> discountMap
    ) {
        if (dto == null || dto.getId() == null || dto.getDefaultVariantId() == null) {
            return dto;
        }

        if (discountMap == null || discountMap.isEmpty()) {
            dto.setHasDiscount(false);
            return dto;
        }

        ActiveProductDiscountDto discount = discountMap.get(dto.getId());
        if (discount == null) {
            dto.setHasDiscount(false);
            return dto;
        }

        dto.setHasDiscount(true);
        dto.setDiscountedPrice(
                computeDiscountedPrice(
                        discount.getDiscountType(),
                        discount.getDiscountValue(),
                        dto.getBasePrice()
                )
        );

        return dto;
    }

    private BigDecimal computeDiscountedPrice(
            DiscountType type,
            BigDecimal value,
            BigDecimal basePrice
    ) {
        if (type == null || value == null || basePrice == null) {
            return basePrice;
        }

        return switch (type) {
            case PERCENT -> basePrice
                    .multiply(BigDecimal.ONE.subtract(
                            value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);

            case FIXED_PRICE -> basePrice.subtract(value)
                    .setScale(2, RoundingMode.HALF_UP);
        };
    }

    private OfferService offerService() {
        return offerServiceProvider.getObject();
    }

}
