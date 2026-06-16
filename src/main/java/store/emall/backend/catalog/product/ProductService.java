package store.emall.backend.catalog.product;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.catalog.product.info.ProductInfoDto;
import store.emall.backend.catalog.product.light.ProductLightDto;
import store.emall.backend.catalog.product.summary.ProductSummary;

import java.util.List;

@Service
public interface ProductService {


    PaginatedResponse<ProductLightDto> getAllLight(ProductFilter filter, Pageable pageable);

    List<ProductLightDto> getAllProductList(ProductFilter filter);

    List<ProductLightDto> getLightByIds(List<Long> productIds);

    List<ProductLightDto> getRandomLight(Integer limit);

    ProductDto create(Long mallId, Long shopId, ProductDto productDto);

    ProductDto update(Long mallId, Long shopId, ProductDto productDto);

    ProductDto getById(Long id, Boolean onlyActive);

    ProductDto getByShopIdAndId(Long shopId, Long id);

    ProductDto getBySlug(String slug);

    ProductDto getByShopIdAndSlug(Long shopId, String slug);

    void delete(Long shopId, Long id);

    ProductInfoDto getProductInfo(Long id);

    ProductSummary getSummary(ProductFilter filter);

    List<ProductLightDto> getSimilar(Long id, Integer topK);
}
