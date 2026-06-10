package store.emall.backend.campaigns.offer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface OfferService {

    PaginatedResponse<OfferDto> getAll(Pageable pageable, Specification<Offer> spec);
    List<OfferDto> getAllOffers(Specification<Offer> spec);
    OfferDto getById(Long offerId);
    List<OfferDto> getByShopId(Long shopId);
    List<OfferDto> getByShopIdAndStatus(Long shopId, OfferStatus status);
    OfferItemDto getActiveOfferItemForProduct(Long productId);
    ActiveProductDiscountDto getActiveDiscountForProduct(Long productId);

    List<ActiveProductDiscountDto> getActiveDiscountsForProducts(List<Long> productIds);
    List<ActiveProductDiscountDto> getPublicActiveSaleProducts(Integer limit);

    OfferDto create(OfferDto dto);
    OfferDto update(OfferDto dto);
    void delete(Long offerId, Long shopId);
    void activate(Long offerId, Long shopId);
    void deactivate(Long offerId, Long shopId);

    OfferDto addProductToOffer(Long offerId, Long shopId, OfferItemDto itemDto);
    OfferDto removeProductFromOffer(Long offerId, Long shopId, Long productId);

    void activateScheduledOffers();
    void expireEndedOffers();
}
