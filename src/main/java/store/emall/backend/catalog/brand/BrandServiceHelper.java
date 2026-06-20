package store.emall.backend.catalog.brand;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.catalog.product.ProductRepository;
import store.emall.backend.common.EntityType;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceHelper {
    private final ProductRepository productRepository;
    private final FileService fileService;
    private final MediaVisibilityService mediaVisibilityService;

    public void activation(Brand brand) {
        // TODO:  notify  vendor how have products attached to this brand
        productRepository.activateProductsByBrandId(brand.getId());
    }


    public void deactivation(Brand brand) {
        productRepository.deactivateProductsByBrandId(brand.getId());
        // TODO:  notify vendor how have products attached to this brand
    }


    public BrandDto injectImageUrl(BrandDto dto) {
        FileDto fileDto = fileService.getById(dto.getImageId());
        dto.setImage(fileDto);
        return dto;

    }

    public void syncBrandImageBinding(Brand brand) {
        mediaVisibilityService.syncPublicBindings(
                EntityType.BRAND,
                brand.getId(),
                "image",
                brand.getImageId() == null ? List.of() : List.of(brand.getImageId())
        );
    }

}
