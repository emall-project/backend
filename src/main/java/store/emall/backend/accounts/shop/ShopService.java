package store.emall.backend.accounts.shop;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface ShopService {

    PaginatedResponse<ShopDto> getAll(Pageable pageable, Specification<Shop> spec);
    List<ShopDto> getAllShops(Specification<Shop> spec);
    ShopDto getById(Long id);
    ShopInfoDto getShopById(Long shopId);

    ShopDto create(ShopDto shopDto);
    ShopDto update(ShopDto shopDto);
    void delete(Long id);

    void activate(Long id);
    void deactivate(Long id);
    void setMaintenance(Long id);

    List<ShopDto> getShopsByMall(Long mallId);
    List<ShopDto> getActiveShopsByMall(Long mallId);
    List<ShopDto> getActiveShops();
    List<ShopDto> getShopsByOwner(Long ownerId);

    void adminBlock(Long id);
    void adminClearBlock(Long id);
    Shop getShopEntityById(Long id);

}