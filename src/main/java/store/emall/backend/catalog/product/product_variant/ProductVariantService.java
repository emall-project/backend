package store.emall.backend.catalog.product.product_variant;

import org.springframework.stereotype.Service;

@Service
public interface ProductVariantService {

    ProductVariantDto create(Long productId, ProductVariantDto dto);

    ProductVariantDto add(Long shopId, Long productId, ProductVariantDto dto);

    ProductVariantDto update(Long shopId, Long productId, ProductVariantDto dto);

    void delete(Long shopId, Long productId, Long id);

}
