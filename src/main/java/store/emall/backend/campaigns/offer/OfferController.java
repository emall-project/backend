package store.emall.backend.campaigns.offer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<PaginatedResponse<OfferDto>> getAll(
            Pageable pageable, OfferSpec spec) {
        return EMallsResponseEntity.ok(offerService.getAll(pageable, spec));
    }

    @GetMapping("/all")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<OfferDto>> getAllOffers(OfferSpec spec) {
        return EMallsResponseEntity.ok(offerService.getAllOffers(spec));
    }

    @GetMapping("/{offerId}")
    @PreAuthorize("@auth.isAdmin() or @auth.isShopOwner()")
    public EMallsResponseEntity<OfferDto> getById(@PathVariable @Positive Long offerId) {
        return EMallsResponseEntity.ok(offerService.getById(offerId));
    }

    @GetMapping("/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<List<OfferDto>> getByShopId(@PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(offerService.getByShopId(shopId));
    }

    @GetMapping("/shop/{shopId}/status/{status}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<List<OfferDto>> getByShopIdAndStatus(
            @PathVariable @Positive Long shopId,
            @PathVariable OfferStatus status) {
        return EMallsResponseEntity.ok(offerService.getByShopIdAndStatus(shopId, status));
    }

    /**
     * Called by catalog microservice to resolve the active discounted price for a product.
     * Returns 204 No Content if no active offer covers the product.
     */
    @GetMapping("/product/{productId}/active-price")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<OfferItemDto> getOfferItemForProduct(
            @PathVariable @Positive Long productId) {
        OfferItemDto item = offerService.getActiveOfferItemForProduct(productId);
        if (item == null) {
            return EMallsResponseEntity.noContent(null);
        }
        return EMallsResponseEntity.ok(item);
    }

    @PostMapping("/products/active-discounts")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<List<ActiveProductDiscountDto>> getActiveDiscountsForProducts(
            @RequestBody @Valid ActiveDiscountsRequest request) {
        return EMallsResponseEntity.ok(
                offerService.getActiveDiscountsForProducts(request.getProductIds())
        );
    }

    @GetMapping("/products/active/public")
    public EMallsResponseEntity<List<ActiveProductDiscountDto>> getPublicActiveSaleProducts(
            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        return EMallsResponseEntity.ok(offerService.getPublicActiveSaleProducts(limit));
    }

    @PostMapping
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<OfferDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) OfferDto dto) {
        return EMallsResponseEntity.created(offerService.create(dto));
    }

    @PutMapping
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<OfferDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) OfferDto dto) {
        return EMallsResponseEntity.ok(offerService.update(dto));
    }

    @DeleteMapping("/{offerId}/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<Void> delete(
            @PathVariable @Positive Long offerId,
            @PathVariable @Positive Long shopId) {
        offerService.delete(offerId, shopId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{offerId}/activate/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<Void> activate(
            @PathVariable @Positive Long offerId,
            @PathVariable @Positive Long shopId) {
        offerService.activate(offerId, shopId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{offerId}/deactivate/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<Void> deactivate(
            @PathVariable @Positive Long offerId,
            @PathVariable @Positive Long shopId) {
        offerService.deactivate(offerId, shopId);
        return EMallsResponseEntity.noContent(null);
    }

    @PostMapping("/{offerId}/shop/{shopId}/products")
    @PreAuthorize("@auth.isShopOwnerOf(#shopId)")
    public EMallsResponseEntity<OfferDto> addProduct(
            @PathVariable @Positive Long offerId,
            @PathVariable @Positive Long shopId,
            @RequestBody @Valid OfferItemDto itemDto) {
        return EMallsResponseEntity.created(offerService.addProductToOffer(offerId, shopId, itemDto));
    }

    @DeleteMapping("/{offerId}/shop/{shopId}/products/{productId}")
    @PreAuthorize("@auth.isShopOwnerOf(#shopId)")
    public EMallsResponseEntity<OfferDto> removeProduct(
            @PathVariable @Positive Long offerId,
            @PathVariable @Positive Long shopId,
            @PathVariable @Positive Long productId) {
        return EMallsResponseEntity.ok(offerService.removeProductFromOffer(offerId, shopId, productId));
    }
}
