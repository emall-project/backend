package store.emall.backend.accounts.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.mall.MallRepository;
import store.emall.backend.accounts.mall.restaurant.MallRestaurant;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantRepository;
import store.emall.backend.accounts.request.shop.ShopRequest;
import store.emall.backend.accounts.request.shop.ShopRequestRepository;
import store.emall.backend.accounts.shop.Shop;
import store.emall.backend.accounts.shop.ShopRepository;
import store.emall.backend.accounts.user.User;
import store.emall.backend.accounts.user.UserRepository;
import store.emall.backend.common.Entity;
import store.emall.backend.common.util.media.MediaUsageDto;
import store.emall.backend.common.util.media.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountsMediaService {
    private final MallRepository mallRepository;
    private final ShopRepository shopRepository;
    private final MallRestaurantRepository mallRestaurantRepository;
    private final ShopRequestRepository shopRequestRepository;
    private final UserRepository userRepository;

    public MediaUsageDto getMediumUsage(UUID mediumId) {
        List<Reference> references = new ArrayList<>();
        boolean inUse = false;

        List<Mall> malls = mallRepository.findByImageId(mediumId);
        if (malls.size() > 0) {
            inUse = true;
            for (Mall mall : malls) {
                Reference reference = Reference.builder()
                        .entity(Entity.MALL)
                        .entityId(mall.getMallId())
                        .entityName(mall.getName())
                        .build();
                references.add(reference);
            }
        }

        List<Shop> shops = shopRepository.findByImageId(mediumId);
        if (shops.size() > 0) {
            inUse = true;
            for (Shop shop : shops) {
                Reference reference = Reference.builder()
                        .entity(Entity.SHOP)
                        .entityId(shop.getShopId())
                        .entityName(shop.getName())
                        .build();
                references.add(reference);
            }
        }

        List<MallRestaurant> restaurants = mallRestaurantRepository.findByImageId(mediumId);
        if (restaurants.size() > 0) {
            inUse = true;
            for (MallRestaurant restaurant : restaurants) {
                Reference reference = Reference.builder()
                        .entity(Entity.MALL_RESTAURANT)
                        .entityId(restaurant.getRestaurantId())
                        .entityName(restaurant.getName())
                        .build();
                references.add(reference);
            }
        }

        List<ShopRequest> shopRequests = shopRequestRepository.findByImageId(mediumId);
        if (shopRequests.size() > 0) {
            inUse = true;
            for (ShopRequest shopRequest : shopRequests) {
                Reference reference = Reference.builder()
                        .entity(Entity.SHOP_REQUEST)
                        .entityId(shopRequest.getId())
                        .entityName(shopRequest.getName())
                        .build();
                references.add(reference);
            }
        }

        List<User> users = userRepository.findByImageId(mediumId);
        if (users.size() > 0) {
            inUse = true;
            for (User user : users) {
                Reference reference = Reference.builder()
                        .entity(Entity.USER)
                        .entityId(user.getUserId())
                        .entityName(user.getFullName() != null ? user.getFullName() : user.getUsername())
                        .build();
                references.add(reference);
            }
        }

        return MediaUsageDto.builder()
                .inUse(inUse)
                .references(references)
                .build();
    }
}