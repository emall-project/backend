package store.emall.backend.accounts.mall.restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.mall.MallExceptions;
import store.emall.backend.accounts.mall.MallRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MallRestaurantServiceImpl implements MallRestaurantService {

    private final MallRestaurantRepository restaurantRepository;
    private final MallRepository mallRepository;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public List<MallRestaurantDto> getRestaurantsByMall(Long mallId) {
        return restaurantRepository.findByMall_MallId(mallId).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallRestaurantDto> getActiveRestaurantsByMall(Long mallId) {
        return restaurantRepository.findByMall_MallIdAndIsActiveTrue(mallId).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallRestaurantDto> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineTypeIgnoreCase(cuisineType).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallRestaurantDto> getRestaurantsByMallAndCuisine(Long mallId, String cuisineType) {
        return restaurantRepository.findByMall_MallIdAndCuisineType(mallId, cuisineType).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MallRestaurantDto getById(Long restaurantId) {
        MallRestaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(MallRestaurantExceptions::restaurantNotFound);
        return toDtoWithMedia(restaurant);
    }

    @Override
    @Transactional
    public MallRestaurantDto addRestaurant(MallRestaurantDto restaurantDto) {
        Long mallId = restaurantDto.getMall().getMallId();
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(MallExceptions::mallNotFound);

        if (restaurantRepository.existsByNameAndMall_MallId(restaurantDto.getName(), mallId)) {
            throw MallRestaurantExceptions.restaurantNameExists();
        }

        // Validate logo if provided
        FileDto logoImage = null;
        if (restaurantDto.getLogoUuid() != null) {
            logoImage = getAndValidateImage(restaurantDto.getLogoUuid());
        }

        MallRestaurant restaurant = MallRestaurantMapper.toEntity(restaurantDto);
        restaurant.setMall(mall);

        MallRestaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant created: restaurantId={}, name={}", saved.getRestaurantId(), saved.getName());

        return MallRestaurantMapper.toFullDto(saved, logoImage);
    }

    @Override
    @Transactional
    public List<MallRestaurantDto> addRestaurants(List<MallRestaurantDto> restaurantDtos) {
        if (restaurantDtos == null || restaurantDtos.isEmpty()) {
            return Collections.emptyList();
        }

        Long commonMallId = restaurantDtos.getFirst().getMall().getMallId();
        boolean allSameMall = restaurantDtos.stream().allMatch(
                dto -> dto.getMall() != null && commonMallId.equals(dto.getMall().getMallId()));

        if (!allSameMall) {
            throw MallRestaurantExceptions.restaurantsBelongToDifferentMalls();
        }

        Mall mall = mallRepository.findById(commonMallId)
                .orElseThrow(MallExceptions::mallNotFound);

        List<MallRestaurant> restaurants = restaurantDtos.stream()
                .peek(dto -> {
                    if (restaurantRepository.existsByNameAndMall_MallId(dto.getName(), commonMallId)) {
                        throw MallRestaurantExceptions.restaurantNameExists();
                    }
                    if (dto.getLogoUuid() != null) {
                        getAndValidateImage(dto.getLogoUuid());
                    }
                })
                .map(dto -> {
                    MallRestaurant restaurant = MallRestaurantMapper.toEntity(dto);
                    restaurant.setMall(mall);
                    return restaurant;
                })
                .collect(Collectors.toList());

        return restaurantRepository.saveAll(restaurants).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MallRestaurantDto updateRestaurant(MallRestaurantDto restaurantDto) {
        MallRestaurant existing = restaurantRepository.findById(restaurantDto.getRestaurantId())
                .orElseThrow(MallRestaurantExceptions::restaurantNotFound);

        if (restaurantDto.getMall() != null) {
            Long newMallId = restaurantDto.getMall().getMallId();
            Long currentMallId = existing.getMall().getMallId();
            if (!newMallId.equals(currentMallId)) {
                if (restaurantRepository.existsByNameAndMall_MallIdAndRestaurantIdNot(
                        restaurantDto.getName(), newMallId, restaurantDto.getRestaurantId())) {
                    throw MallRestaurantExceptions.restaurantNameExists();
                }

                Mall newMall = mallRepository.findById(newMallId)
                        .orElseThrow(MallExceptions::mallNotFound);

                existing.setMall(newMall);
            }
        }

        if (restaurantDto.getName() != null
                && !restaurantDto.getName().equals(existing.getName())
                && restaurantRepository.existsByNameAndMall_MallIdAndRestaurantIdNot(
                restaurantDto.getName(), existing.getMall().getMallId(), restaurantDto.getRestaurantId())) {
            throw MallRestaurantExceptions.restaurantNameExists();
        }

        // Validate new logo if provided and different
        if (restaurantDto.getLogoUuid() != null && !restaurantDto.getLogoUuid().equals(existing.getLogoUuid())) {
            getAndValidateImage(restaurantDto.getLogoUuid());
        }

        MallRestaurantMapper.merge(existing, restaurantDto);
        MallRestaurant saved = restaurantRepository.save(existing);

        log.info("Restaurant updated: restaurantId={}", saved.getRestaurantId());

        return toDtoWithMedia(saved);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        MallRestaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(MallRestaurantExceptions::restaurantNotFound);
        restaurantRepository.delete(restaurant);
        log.info("Restaurant deleted: restaurantId={}", restaurantId);
    }

    @Override
    @Transactional
    public void deleteAllRestaurantsByMall(Long mallId) {
        restaurantRepository.deleteByMall_MallId(mallId);
        log.info("All restaurants deleted for mallId={}", mallId);
    }

    @Override
    @Transactional
    public void activateRestaurant(Long restaurantId) {
        MallRestaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(MallRestaurantExceptions::restaurantNotFound);

        if (Boolean.TRUE.equals(restaurant.getIsActive())) {
            return;
        }
        restaurant.setIsActive(Boolean.TRUE);
        restaurantRepository.save(restaurant);
        log.info("Restaurant activated: restaurantId={}", restaurantId);
    }

    @Override
    @Transactional
    public void deactivateRestaurant(Long restaurantId) {
        MallRestaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(MallRestaurantExceptions::restaurantNotFound);

        if (Boolean.FALSE.equals(restaurant.getIsActive())) {
            return;
        }
        restaurant.setIsActive(Boolean.FALSE);
        restaurantRepository.save(restaurant);
        log.info("Restaurant deactivated: restaurantId={}", restaurantId);
    }

    private MallRestaurantDto toDtoWithMedia(MallRestaurant restaurant) {
        FileDto logoImage = fetchImageSafely(restaurant.getLogoUuid());
        return MallRestaurantMapper.toFullDto(restaurant, logoImage);
    }

    private FileDto fetchImageSafely(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return fileService.getById(uuid);
    }

    private FileDto getAndValidateImage(UUID uuid) {
        FileDto fileDto = fileService.getById(uuid);
        if (!isImage(fileDto.getMimeType())) {
            throw MallRestaurantExceptions.invalidFileType();
        }
        return fileDto;
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }
}