package store.emall.backend.catalog.product;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.catalog.product.light.ProductLightDto;
import store.emall.backend.catalog.product.product_variant.ProductVariantDto;
import store.emall.backend.catalog.product.product_variant.ProductVariantService;
import store.emall.backend.catalog.product.summary.ProductSummary;
import store.emall.backend.security.SecurityContextUtilBean;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("stores/{shopId}/products")
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
@RequiredArgsConstructor
public class ShopProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final SecurityContextUtilBean auth;


    @PostMapping("/all")
    public EMallsResponseEntity<PaginatedResponse<ProductLightDto>> getAllLight(@PathVariable Long shopId, @RequestBody ProductFilter filter, Pageable pageable) {
        filter.setShopId(shopId);
        PaginatedResponse<ProductLightDto> products = productService.getAllLight(filter, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @PostMapping("/summary")
    public EMallsResponseEntity<ProductSummary> getSummary(@PathVariable Long shopId, @RequestBody ProductFilter filter) {
        filter.setShopId(shopId);
        ProductSummary productsSummary = productService.getSummary(filter);
        return EMallsResponseEntity.ok(productsSummary);
    }

    @PostMapping("/all/list")
    public EMallsResponseEntity<List<ProductLightDto>> getAllList(@PathVariable Long shopId, @RequestBody ProductFilter filter) {
        filter.setShopId(shopId);
        List<ProductLightDto> products = productService.getAllProductList(filter);
        return EMallsResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<ProductDto> getById(@PathVariable Long shopId, @PathVariable Long id) {
        ProductDto dto = productService.getByShopIdAndId(shopId, id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<ProductDto> getBySlug(@PathVariable Long shopId, @PathVariable String slug) {
        ProductDto dto = productService.getByShopIdAndSlug(shopId, slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    public EMallsResponseEntity<ProductDto> create(@PathVariable Long shopId, @RequestBody @Validated({Default.class, OnCreate.class}) ProductDto dto) {
        Long mallId = auth.getMallId(shopId);
        ProductDto created = productService.create(mallId, shopId, dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    public EMallsResponseEntity<ProductDto> update(@PathVariable Long shopId, @RequestBody @Validated({Default.class, OnUpdate.class}) ProductDto dto) {
        Long mallId = auth.getMallId(shopId);
        ProductDto updated = productService.update(mallId, shopId, dto);
        return EMallsResponseEntity.ok(updated);
    }

    @PutMapping("{productId}/variants")
    public EMallsResponseEntity<ProductVariantDto> updateVariant(@PathVariable Long shopId, @PathVariable Long productId, @RequestBody @Validated({Default.class, OnUpdate.class}) ProductVariantDto dto) {
        ProductVariantDto updated = productVariantService.add(shopId, productId, dto);
        return EMallsResponseEntity.ok(updated);
    }

    @PostMapping("{productId}/variants")
    public EMallsResponseEntity<ProductVariantDto> addVariant(@PathVariable Long shopId, @PathVariable Long productId, @RequestBody @Validated({Default.class, OnCreate.class}) ProductVariantDto dto) {
        ProductVariantDto updated = productVariantService.update(shopId, productId, dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long shopId, @PathVariable Long id) {
        productService.delete(shopId, id);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/{productId}/variants/{id}")
    public EMallsResponseEntity<Void> deleteVariant(@PathVariable Long shopId, @PathVariable Long productId, @PathVariable Long id) {
        productVariantService.delete(shopId, productId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
