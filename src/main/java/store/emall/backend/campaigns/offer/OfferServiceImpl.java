package store.emall.backend.campaigns.offer;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.shop.ShopInfoDto;
import store.emall.backend.accounts.shop.ShopService;
import store.emall.backend.campaigns.ad.request.AdRequestExceptions;
import store.emall.backend.catalog.product.ProductService;
import store.emall.backend.catalog.product.info.ProductInfoDto;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.security.SecurityContextUtil;
import store.emall.backend.campaigns.subscription.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferItemRepository offerItemRepository;
    private final ShopService shopService;
    private final ProductService productService;
    private final ShopSubscriptionService subscriptionService;
    private final ShopSubscriptionRepository subscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<OfferDto> getAll(Pageable pageable, Specification<Offer> spec) {
        Page<OfferDto> page = offerRepository.findAll(spec, pageable)
                .map(offer -> toDtoWithFullInfo(offer, offer));
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferDto> getAllOffers(Specification<Offer> spec) {
        List<Offer> offers = (spec == null)
                ? offerRepository.findAll()
                : offerRepository.findAll(spec);
        return offers.stream()
                .map(offer -> toDtoWithFullInfo(offer, offer))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OfferDto getById(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(OfferExceptions::offerNotFound);

        if (!SecurityContextUtil.isShopOwnerOf(offer.getShopId())) {
            throw AdRequestExceptions.requestNotFoundForShop();
        }

        return toDtoWithFullInfo(offer, offer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferDto> getByShopId(Long shopId) {
        return offerRepository.findByShopId(shopId).stream()
                .map(offer -> toDtoWithFullInfo(offer, offer))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferDto> getByShopIdAndStatus(Long shopId, OfferStatus status) {
        return offerRepository.findByShopIdAndStatus(shopId, status).stream()
                .map(offer -> toDtoWithFullInfo(offer, offer))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OfferItemDto getActiveOfferItemForProduct(Long productId) {
        List<OfferItem> activeItems = offerItemRepository
                .findActiveOfferItemsForProduct(productId, LocalDateTime.now());

        if (activeItems.isEmpty()) {
            // No active offer
            return null;
        }

        OfferItem best = activeItems.get(0);
        OfferItemDto itemDto = OfferMapper.toItemDto(best);

        Offer offer = offerRepository.findById(itemDto.getOfferId())
                .orElseThrow(OfferExceptions::offerNotFound);
        itemDto.setOffer(OfferMapper.toDto(offer));

        populateProductInfo(itemDto, best.getOffer());
        return itemDto;
    }
    @Override
    @Transactional(readOnly = true)
    public ActiveProductDiscountDto getActiveDiscountForProduct(Long productId) {
        List<OfferItem> activeItems = offerItemRepository
                .findActiveOfferItemsForProduct(productId, LocalDateTime.now());

        if (activeItems.isEmpty()) {
            // No active offer
            return null;
        }

        OfferItem best = activeItems.get(0);
        OfferItemDto itemDto = OfferMapper.toItemDto(best);

        Offer offer = offerRepository.findById(itemDto.getOfferId())
                .orElseThrow(OfferExceptions::offerNotFound);
        itemDto.setOffer(OfferMapper.toDto(offer));

        return ActiveProductDiscountDto.builder()
                .productId(best.getProductId())
                .offerId(offer.getOfferId())
                .discountType(offer.getDiscountType())
                .discountValue(offer.getDiscountValue())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveProductDiscountDto> getActiveDiscountsForProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        List<Long> sanitizedProductIds = productIds.stream()
                .filter(java.util.Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .toList();

        if (sanitizedProductIds.isEmpty()) {
            return List.of();
        }

        List<OfferItem> items = offerItemRepository.findResolvedActiveDiscountItemsForProducts(
                sanitizedProductIds,
                LocalDateTime.now()
        );

        // Defensive deduplication:
        // business rules should prevent multiple active overlapping offers for the same product,
        // but if bad data exists, we keep the first row from the ordered query.
        Map<Long, ActiveProductDiscountDto> resolved = new java.util.LinkedHashMap<>();

        for (OfferItem item : items) {
            Offer offer = item.getOffer();
            if (offer == null) {
                continue;
            }

            resolved.putIfAbsent(
                    item.getProductId(),
                    ActiveProductDiscountDto.builder()
                            .productId(item.getProductId())
                            .offerId(offer.getOfferId())
                            .discountType(offer.getDiscountType())
                            .discountValue(offer.getDiscountValue())
                            .build()
            );
        }

        return new java.util.ArrayList<>(resolved.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveProductDiscountDto> getPublicActiveSaleProducts(Integer limit) {
        int safeLimit = limit == null || limit < 1 ? 10 : Math.min(limit, 50);
        List<OfferItem> items = offerItemRepository.findPublicActiveOfferItems(
                LocalDateTime.now(),
                PageRequest.of(0, Math.min(safeLimit * 3, 150))
        );

        Map<Long, ActiveProductDiscountDto> resolved = new java.util.LinkedHashMap<>();

        for (OfferItem item : items) {
            Offer offer = item.getOffer();
            if (offer == null) {
                continue;
            }

            resolved.putIfAbsent(
                    item.getProductId(),
                    ActiveProductDiscountDto.builder()
                            .productId(item.getProductId())
                            .offerId(offer.getOfferId())
                            .discountType(offer.getDiscountType())
                            .discountValue(offer.getDiscountValue())
                            .build()
            );

            if (resolved.size() >= safeLimit) {
                break;
            }
        }

        return new java.util.ArrayList<>(resolved.values());
    }


    @Override
    @Transactional
    public OfferDto create(OfferDto dto) {
        // Validate shop exists and is active via accounts microservice
        validateShopExists(dto.getShopId());

        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(dto.getShopId())) {
                throw OfferExceptions.shopNotFound();
            }
            validateSubscriptionWriteAccess(dto.getShopId());
        }

        // Offer title must be unique per shop
        if (offerRepository.existsByTitleAndShopId(dto.getTitle(), dto.getShopId())) {
            throw OfferExceptions.offerTitleExists();
        }

        // startDate must not be in the past
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw OfferExceptions.offerStartDateInPast();
        }

        // endDate must be strictly after startDate
        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw OfferExceptions.offerEndDateNotAfterStart();
        }

        // Validate discount value rules
        validateDiscountValue(dto.getDiscountType(), dto.getDiscountValue());

        // Build offer entity (status = INACTIVE by default)
        Offer offer = OfferMapper.toEntity(dto);

        // Process each item — validate product exists in catalog
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {

            for (OfferItemDto itemDto : dto.getItems()) {
                OfferItem item = buildOfferItem(offer, itemDto);
                offer.getItems().add(item);
            }

            // Ensure that the same product is not include in two offers with overlapping dates
            List<Long> productIds = offer.getItems().stream()
                    .map(OfferItem::getProductId)
                    .toList();

            if (!productIds.isEmpty()) {
                List<OfferItem> overlappingItems = offerItemRepository.findOverlappingOffersForProducts(
                        productIds,
                        null,
                        dto.getStartDate(),
                        dto.getEndDate()
                );


                if (!overlappingItems.isEmpty()) {
                    Map<String, String> productToOffer = overlappingItems.stream()
                            .collect(Collectors.toMap(
                                    item -> fetchProductInfo(item.getProductId()).getName(),
                                    item -> item.getOffer().getTitle(),
                                    (existing, replacement) -> existing
                            ));

                    StringBuilder messageBuilder = new StringBuilder();
                    productToOffer.forEach((productName, offerTitle) -> {
                        messageBuilder.append("Product '")
                                .append(productName)
                                .append("' is already in active offer '")
                                .append(offerTitle)
                                .append("'. ");
                    });

                    String finalMessage = messageBuilder.toString().trim();
                    throw OfferExceptions.productAlreadyInOverlappingOffer(finalMessage);
                }
            }

        }

        Offer saved = offerRepository.save(offer);
        log.info("Offer created: id={}, shopId={}, title='{}', items={}",
                saved.getOfferId(), saved.getShopId(), saved.getTitle(), saved.getItems().size());

        return toDtoWithFullInfo(saved, saved);
    }

    @Override
    @Transactional
    public OfferDto update(OfferDto dto) {
        Offer existing = offerRepository.findById(dto.getOfferId())
                .orElseThrow(OfferExceptions::offerNotFound);

        // Ownership check — only the owning shop can update
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(existing.getShopId())) {
                throw OfferExceptions.offerNotOwnedByShop();
            }
            validateSubscriptionWriteAccess(existing.getShopId());
        }

        // EXPIRED offers are fully immutable
        if (existing.getStatus() == OfferStatus.EXPIRED) {
            throw OfferExceptions.offerExpired();
        }

        // ACTIVE offers: only title, description, endDate, maxUses can change.
        // Changing discountType or discountValue on a live offer requires deactivation first -> because it would immediately change prices customers are seeing.
        if (existing.getStatus() == OfferStatus.ACTIVE) {
            boolean hasPricingChange = dto.getDiscountType() != null
                    || dto.getDiscountValue() != null
                    || dto.getStartDate() != null;

            if (hasPricingChange) {
                throw OfferExceptions.cannotModifyActiveOffer();
            }
        }

        // Title uniqueness check (exclude self)
        if (dto.getTitle() != null
                && offerRepository.existsByTitleAndShopIdAndOfferIdNot(
                dto.getTitle(), existing.getShopId(), existing.getOfferId())) {
            throw OfferExceptions.offerTitleExists();
        }

        LocalDateTime newStart = dto.getStartDate() != null ? dto.getStartDate() : existing.getStartDate();
        LocalDateTime newEnd = dto.getEndDate() != null ? dto.getEndDate() : existing.getEndDate();

        // startDate must not be moved into the past
        if (dto.getStartDate() != null && dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw OfferExceptions.offerStartDateInPast();
        }

        // endDate must remain strictly after startDate
        if (!newEnd.isAfter(newStart)) {
            throw OfferExceptions.offerEndDateNotAfterStart();
        }

        // Validate discount value if being changed (INACTIVE only —> ACTIVE blocked above)
        if (dto.getDiscountType() != null || dto.getDiscountValue() != null) {
            DiscountType newType = dto.getDiscountType() != null ? dto.getDiscountType() : existing.getDiscountType();
            BigDecimal newValue = dto.getDiscountValue() != null ? dto.getDiscountValue() : existing.getDiscountValue();
            validateDiscountValue(newType, newValue);

            // FOR FIXED_PRICE -> re-validate against all current offer items
            if (newType == DiscountType.FIXED_PRICE) {
                existing.getItems().stream()
                        .filter(item -> item.getStatus() == OfferItemStatus.ACTIVE)
                        .forEach(item -> {
                            ProductInfoDto product = fetchProductInfo(item.getProductId());
                            product.getVariants().forEach(variant -> {
                                if (newValue.compareTo(variant.getBasePrice()) >= 0) {
                                    throw OfferExceptions.discountExceedsPrice();
                                }
                            });
                        });
            }
        }

        // Ensure that the same product is not include in two offers with overlapping dates
        List<Long> productIds = existing.getItems().stream()
                .filter(item -> item.getStatus() == OfferItemStatus.ACTIVE)
                .map(OfferItem::getProductId)
                .toList();

        if (!productIds.isEmpty()) {
            List<OfferItem> overlappingItems = offerItemRepository.findOverlappingOffersForProducts(
                    productIds,
                    existing.getOfferId(),
                    newStart,
                    newEnd
            );

            if (!overlappingItems.isEmpty()) {
                Map<String, String> productToOffer = overlappingItems.stream()
                        .collect(Collectors.toMap(
                                item -> fetchProductInfo(item.getProductId()).getName(),
                                item -> item.getOffer().getTitle(),
                                (oldValue, newValue) -> oldValue
                        ));

                StringBuilder messageBuilder = new StringBuilder();
                productToOffer.forEach((productName, offerTitle) -> {
                    messageBuilder.append("Product '")
                            .append(productName)
                            .append("' is already in active offer '")
                            .append(offerTitle)
                            .append("'. ");
                });

                String finalMessage = messageBuilder.toString().trim();
                throw OfferExceptions.productAlreadyInOverlappingOffer(finalMessage);
            }
        }

        OfferMapper.merge(existing, dto);
        Offer saved = offerRepository.save(existing);
        log.info("Offer updated: id={}, shopId={}", saved.getOfferId(), saved.getShopId());

        return toDtoWithFullInfo(saved, saved);
    }

    @Override
    @Transactional
    public void delete(Long offerId, Long shopId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(OfferExceptions::offerNotFound);

        // Ownership check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(offer.getShopId())) {
                throw OfferExceptions.offerNotOwnedByShop();
            }
            validateSubscriptionWriteAccess(offer.getShopId());
        }

        // Cannot delete an ACTIVE offer —> deactivate it first
        if (offer.getStatus() == OfferStatus.ACTIVE) {
            throw OfferExceptions.cannotModifyActiveOffer();
        }

        offerRepository.delete(offer);
        log.info("Offer deleted: id={}, shopId={}", offerId, shopId);
    }

    //TODO: Hidden from shop owner for security
    @Override
    @Transactional
    public void activate(Long offerId, Long shopId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(OfferExceptions::offerNotFound);

        // Ownership check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(offer.getShopId())) {
                throw OfferExceptions.offerNotOwnedByShop();
            }
            validateSubscriptionWriteAccess(offer.getShopId());
        }

        // Cannot activate an EXPIRED offer
        if (offer.getStatus() == OfferStatus.EXPIRED) {
            throw OfferExceptions.offerAlreadyExpired();
        }

        // Already ACTIVE —> nothing to do
        if (offer.getStatus() == OfferStatus.ACTIVE) {
            return;
        }

        // endDate must not have already passed
        if (offer.getEndDate().isBefore(LocalDateTime.now())) {
            throw OfferExceptions.offerAlreadyExpired();
        }

        // Must have at least one ACTIVE item before going live
        boolean hasActiveItems = offer.getItems().stream()
                .anyMatch(item -> item.getStatus() == OfferItemStatus.ACTIVE);
        if (!hasActiveItems) {
            throw OfferExceptions.noActiveItemsInOffer();
        }

        // Ensure that the same product is not include in two offers with overlapping dates
        List<Long> productIds = offer.getItems().stream()
                .filter(item -> item.getStatus() == OfferItemStatus.ACTIVE)
                .map(OfferItem::getProductId)
                .toList();

        if (!productIds.isEmpty()) {
            List<OfferItem> overlappingItems = offerItemRepository.findOverlappingOffersForProducts(
                    productIds,
                    offer.getOfferId(),
                    LocalDateTime.now(),
                    offer.getEndDate()
            );

            if (!overlappingItems.isEmpty()) {
                Map<String, String> productToOffer = overlappingItems.stream()
                        .collect(Collectors.toMap(
                                item -> fetchProductInfo(item.getProductId()).getName(),
                                item -> item.getOffer().getTitle(),
                                (oldValue, newValue) -> oldValue
                        ));

                StringBuilder messageBuilder = new StringBuilder();
                productToOffer.forEach((productName, offerTitle) -> {
                    messageBuilder.append("Product '")
                            .append(productName)
                            .append("' is already in active offer '")
                            .append(offerTitle)
                            .append("'. ");
                });

                String finalMessage = messageBuilder.toString().trim();
                throw OfferExceptions.productAlreadyInOverlappingOffer(finalMessage);
            }
        }

        offer.setStartDate(LocalDateTime.now());
        offer.setStatus(OfferStatus.ACTIVE);
        offerRepository.save(offer);
        log.info("Offer activated: id={}, shopId={}", offerId, shopId);
    }

    @Override
    @Transactional
    public void deactivate(Long offerId, Long shopId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(OfferExceptions::offerNotFound);

        // Ownership check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(offer.getShopId())) {
                throw OfferExceptions.offerNotOwnedByShop();
            }
            validateSubscriptionWriteAccess(offer.getShopId());
        }

        // Cannot deactivate an EXPIRED offer (status is final)
        if (offer.getStatus() == OfferStatus.EXPIRED) {
            throw OfferExceptions.offerExpired();
        }

        offer.setStatus(OfferStatus.INACTIVE);
        offerRepository.save(offer);
        log.info("Offer deactivated: id={}, shopId={}", offerId, shopId);
    }

    @Override
    @Transactional
    public OfferDto addProductToOffer(Long offerId, Long shopId, OfferItemDto itemDto) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(OfferExceptions::offerNotFound);

        // Ownership check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(offer.getShopId())) {
                throw OfferExceptions.offerNotOwnedByShop();
            }
            validateSubscriptionWriteAccess(offer.getShopId());
        }

        // Cannot add products to an EXPIRED offer
        if (offer.getStatus() == OfferStatus.EXPIRED) {
            throw OfferExceptions.offerExpired();
        }

        // Product must not already be in this offer (even if previously REMOVED)
        if (offerItemRepository.existsByOffer_OfferIdAndProductId(offerId, itemDto.getProductId())) {
            throw OfferExceptions.productAlreadyInOffer();
        }


        // Check for overlapping offers with the new product
        List<Long> productIds = List.of(itemDto.getProductId());
        List<OfferItem> overlappingItems = offerItemRepository.findOverlappingOffersForProducts(
                productIds,
                offer.getOfferId(),
                offer.getStartDate(),
                offer.getEndDate()
        );

        if (!overlappingItems.isEmpty()) {
            Map<String, String> productToOffer = overlappingItems.stream()
                    .collect(Collectors.toMap(
                            item -> fetchProductInfo(item.getProductId()).getName(),
                            item -> item.getOffer().getTitle(),
                            (existing, replacement) -> existing
                    ));

            StringBuilder messageBuilder = new StringBuilder();
            productToOffer.forEach((productName, offerTitle) -> {
                messageBuilder.append("Product '")
                        .append(productName)
                        .append("' is already in active offer '")
                        .append(offerTitle)
                        .append("'. ");
            });
            String finalMessage = messageBuilder.toString().trim();
            throw OfferExceptions.productAlreadyInOverlappingOffer(finalMessage);
        }

        OfferItem item = buildOfferItem(offer, itemDto);
        offer.getItems().add(item);

        Offer saved = offerRepository.save(offer);
        log.info("Product {} added to offer {}", itemDto.getProductId(), offerId);

        return toDtoWithFullInfo(saved, saved);
    }

    @Override
    @Transactional
    public OfferDto removeProductFromOffer(Long offerId, Long shopId, Long productId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(OfferExceptions::offerNotFound);

        // Ownership check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(offer.getShopId())) {
                throw OfferExceptions.offerNotOwnedByShop();
            }
            validateSubscriptionWriteAccess(offer.getShopId());
        }

        // Cannot remove from an EXPIRED offer
        if (offer.getStatus() == OfferStatus.EXPIRED) {
            throw OfferExceptions.offerExpired();
        }

        OfferItem item = offerItemRepository.findByOffer_OfferIdAndProductId(offerId, productId)
                .orElseThrow(OfferExceptions::offerItemNotFound);

        // Soft remove
        item.setStatus(OfferItemStatus.REMOVED);
        offerItemRepository.save(item);

        // If last ACTIVE item removed from a live offer → auto-deactivate
        if (offer.getStatus() == OfferStatus.ACTIVE) {
            boolean hasRemainingActive = offer.getItems().stream()
                    .anyMatch(i -> i.getStatus() == OfferItemStatus.ACTIVE
                            && !i.getProductId().equals(productId));
            if (!hasRemainingActive) {
                offer.setStatus(OfferStatus.INACTIVE);
                offerRepository.save(offer);
                log.warn("Offer {} auto-deactivated: last active product was removed", offerId);
            }
        }

        log.info("Product {} soft-removed from offer {}", productId, offerId);
        return toDtoWithFullInfo(
                offerRepository.findById(offerId).orElseThrow(),
                offerRepository.findById(offerId).orElseThrow());
    }

    @Override
    @Transactional
    public void activateScheduledOffers() {
        List<Offer> toActivate = offerRepository.findOffersToActivate(OfferStatus.INACTIVE, LocalDateTime.now());

        toActivate.forEach(offer -> {
            boolean hasActiveItems = offer.getItems().stream()
                    .anyMatch(item -> item.getStatus() == OfferItemStatus.ACTIVE);

            if (hasActiveItems) {
                offer.setStatus(OfferStatus.ACTIVE);
                log.info("Auto-activated offer: id={}, shopId={}, title='{}'",
                        offer.getOfferId(), offer.getShopId(), offer.getTitle());
            } else {
                log.warn("Skipped auto-activation for offer {}: no active items", offer.getOfferId());
            }
        });

        if (!toActivate.isEmpty()) {
            offerRepository.saveAll(toActivate);
        }
    }

    @Override
    @Transactional
    public void expireEndedOffers() {
        List<Offer> expired = offerRepository.findExpiredActiveOffers(LocalDateTime.now());

        expired.forEach(offer -> {
            offer.setStatus(OfferStatus.EXPIRED);
            log.info("Auto-expired offer: id={}, shopId={}, title='{}'",
                    offer.getOfferId(), offer.getShopId(), offer.getTitle());
        });

        if (!expired.isEmpty()) {
            offerRepository.saveAll(expired);
        }
    }

    /**
     * Maps Offer → OfferDto and enriches:
     * - ShopInfoDto via Feign from accounts service
     * - ProductInfoDto + VariantPriceDto per item via Feign from catalog service
     */
    private OfferDto toDtoWithFullInfo(Offer entity, Offer offerForDiscount) {
        OfferDto dto = OfferMapper.toDto(entity);
        if (dto == null) return null;

        // Populate full shop info
        if (dto.getShopId() != null) {
            ShopInfoDto shop = shopService.getShopById(dto.getShopId());
            dto.setShop(shop);
        }

        // Populate product info + compute variant prices for each item
        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDto -> populateProductInfo(itemDto, offerForDiscount));
        }

        return dto;
    }

    /**
     * Fetches ProductInfoDto from catalog and computes discounted price
     * for EACH variant individually using the offer's discount rule.
     */
    private void populateProductInfo(OfferItemDto itemDto, Offer offer) {
        if (itemDto == null || itemDto.getProductId() == null) return;

        ProductInfoDto product = productService.getProductInfo(itemDto.getProductId());

        itemDto.setProduct(product);

        // Compute discounted price for each variant individually
        if (product.getVariants() != null) {
            List<VariantPriceDto> variantPrices = product.getVariants().stream()
                    .map(variant -> VariantPriceDto.builder()
                            .variantId(variant.getVariantId())
                            .variantName(variant.getVariantName())
                            .originalPrice(variant.getBasePrice())
                            .discountedPrice(computeDiscountedPrice(
                                    offer.getDiscountType(),
                                    offer.getDiscountValue(),
                                    variant.getBasePrice()))
                            .isDefault(variant.getIsDefault())
                            .discountValue(offer.getDiscountValue())
                            .discountType(offer.getDiscountType().toString())
                            .build())
                    .collect(Collectors.toList());

            itemDto.setVariantPrices(variantPrices);
        }

    }

    /**
     * Builds an OfferItem — validates product exists, is active,
     * AND for FIXED_PRICE offers validates the discount does not equal or exceed ANY variant's price.
     * <p>
     * We validate against ALL variants because the offer applies to all of them — if even one variant would be free or negative,
     * we reject the offer creation.
     */
    private OfferItem buildOfferItem(Offer offer, OfferItemDto itemDto) {
        ProductInfoDto product = fetchProductInfo(itemDto.getProductId());

        if (offer.getDiscountType() == DiscountType.FIXED_PRICE) {
            product.getVariants().forEach(variant -> {
                if (offer.getDiscountValue().compareTo(variant.getBasePrice()) >= 0) {
                    log.warn("FIXED_PRICE discount {} >= variant '{}' price {}. Rejecting offer item.",
                            offer.getDiscountValue(), variant.getVariantName(), variant.getBasePrice());
                    throw OfferExceptions.discountExceedsPrice();
                }
            });
        }
        return OfferItem.builder()
                .offer(offer)
                .productId(itemDto.getProductId())
                .status(OfferItemStatus.ACTIVE)
                .build();
    }

    private ProductInfoDto fetchProductInfo(Long productId) {
        ProductInfoDto product = productService.getProductInfo(productId);
        if (Boolean.FALSE.equals(product.getIsActive())) {
            throw OfferExceptions.productNotFound();
        }
        return product;
    }


    private void validateProductExists(Long productId) {
        ProductInfoDto product = productService.getProductInfo(productId);
        if (Boolean.FALSE.equals(product.getIsActive())) {
            // TODO: add isActive() to product service
            throw OfferExceptions.productNotFound();
        }
    }

    /**
     * Validates discount value business rules at offer level:
     * - PERCENT: must be between 1 and 99 inclusive.
     * (0% = no discount, 100% = free → both rejected)
     * - FIXED_PRICE: positivity enforced by DTO @DecimalMin(0.01).
     * Per-variant validation happens in buildOfferItem() where
     * we have access to actual prices.
     */
    private void validateDiscountValue(DiscountType type, BigDecimal value) {
        if (type == DiscountType.PERCENT) {
            if (value.compareTo(BigDecimal.ONE) < 0
                    || value.compareTo(BigDecimal.valueOf(99)) > 0) {
                throw OfferExceptions.invalidPercentValue();
            }
        }
    }

    /**
     * Computes the final discounted price for a single variant's basePrice :
     * PERCENT: discountedPrice = basePrice × (1 − discountValue / 100)
     * FIXED_PRICE: discountedPrice = basePrice − discountValue
     */
    private BigDecimal computeDiscountedPrice(DiscountType type, BigDecimal value, BigDecimal basePrice) {
        if (basePrice == null) return BigDecimal.ZERO;
        return switch (type) {
            case PERCENT -> basePrice
                    .multiply(BigDecimal.ONE.subtract(
                            value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);
            case FIXED_PRICE -> basePrice.subtract(value)
                    .setScale(2, RoundingMode.HALF_UP);
        };
    }

    private void validateShopExists(Long shopId) {
        ShopInfoDto shop = shopService.getShopById(shopId);
        if (shop == null) {
            throw OfferExceptions.shopNotFound();
        }
        if (Boolean.FALSE.equals(shop.getIsActive())) {
            throw OfferExceptions.shopNotFound();
        }
    }

    private void validateSubscriptionWriteAccess(Long shopId) {
        // 1. Check subscription status
        ShopSubscription sub = subscriptionRepository.findByShopId(shopId).orElse(null);
        if (sub == null || sub.getStatus() == SubscriptionStatus.EXPIRED
                || sub.getStatus() == SubscriptionStatus.CANCELLED) {
            throw SubscriptionExceptions.subscriptionWriteAccessDenied();
        }

        // 2. Check admin block — delegated to accounts via isEffectivelyActive()
        // The accounts service already embeds adminStatus logic in hasWriteAccess
        try {
//            AccountsResponse<Boolean> writeAccessResponse = accountsClient.hasWriteAccess(shopId);
//            Boolean canWrite = writeAccessResponse != null ? writeAccessResponse.getData() : null;

//            if (Boolean.FALSE.equals(canWrite)) {
//                throw SubscriptionExceptions.shopBlocked();
//            }
        } catch (FeignException e) {
            // Graceful degradation — if accounts is down, fall back to subscription check only
            log.warn("Could not verify admin write-access for shopId={}, proceeding with subscription check only", shopId);
        }
    }
}
