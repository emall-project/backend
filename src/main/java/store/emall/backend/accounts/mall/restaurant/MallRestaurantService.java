package store.emall.backend.accounts.mall.restaurant;

import java.util.List;

public interface MallRestaurantService {

    List<MallRestaurantDto> getRestaurantsByMall(Long mallId);
    List<MallRestaurantDto> getActiveRestaurantsByMall(Long mallId);
    List<MallRestaurantDto> getRestaurantsByCuisine(String cuisineType);
    List<MallRestaurantDto> getRestaurantsByMallAndCuisine(Long mallId, String cuisineType);
    MallRestaurantDto getById(Long restaurantId);

    MallRestaurantDto addRestaurant(MallRestaurantDto restaurantDto);
    List<MallRestaurantDto> addRestaurants(List<MallRestaurantDto> restaurantDtos);
    MallRestaurantDto updateRestaurant(MallRestaurantDto restaurantDto);

    void deleteRestaurant(Long restaurantId);
    void deleteAllRestaurantsByMall(Long mallId);

    void activateRestaurant(Long restaurantId);
    void deactivateRestaurant(Long restaurantId);

}
