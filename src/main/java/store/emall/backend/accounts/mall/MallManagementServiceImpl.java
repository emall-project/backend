package store.emall.backend.accounts.mall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.city.City;
import store.emall.backend.accounts.city.CityExceptions;
import store.emall.backend.accounts.city.CityRepository;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.accounts.mall.dtos.MallDto;
import store.emall.backend.accounts.mall.restaurant.MallRestaurant;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantDto;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantMapper;
import store.emall.backend.accounts.mall.service.MallServiceEntity;
import store.emall.backend.accounts.mall.service.MallServiceMapper;
import store.emall.backend.accounts.shop.ShopRepository;
import store.emall.backend.accounts.shop.ShopStatus;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MallManagementServiceImpl implements MallManagementService {

    private final MallRepository mallRepository;
    private final CityRepository cityRepository;
    private final FileService fileService;
    private final ShopRepository shopRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<MallDto> getAll(Pageable pageable, Specification<Mall> spec) {
        Page<MallDto> mallPage = mallRepository.findAll(spec, pageable)
                .map(this::toDtoWithMedia);
        return PaginatedResponse.of(mallPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> getAllMalls(Specification<Mall> spec) {
        List<Mall> malls = (spec == null)
                ? mallRepository.findAll()
                : mallRepository.findAll(spec);

        return malls.stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MallDto getById(Long id) {
        Mall mall = mallRepository.findById(id)
                .orElseThrow(MallExceptions::mallNotFound);
        return toDtoWithMedia(mall);
    }

    @Override
    @Transactional
    public MallDto create(MallDto mallDto) {
        if (mallDto.getCity() == null || mallDto.getCity().getCityId() == null) {
            throw CityExceptions.cityIdRequired();
        }

        City city = cityRepository.findById(mallDto.getCity().getCityId())
                .orElseThrow(CityExceptions::cityNotFound);

        // Check if mall name already exists in the same city
        if (mallRepository.existsByNameAndCity_CityId(mallDto.getName(), city.getCityId())) {
            throw MallExceptions.mallNameExistsInCity();
        }

        // Validate logo image if provided
        FileDto logoImage = null;
        if (mallDto.getLogoUuid() != null) {
            logoImage = getAndValidateImage(mallDto.getLogoUuid());
        }

        // Validate mall images if provided
        List<FileDto> mallImages = getAndValidateImages(mallDto.getMallImagesUuids());

        Mall mall = MallMapper.toEntity(mallDto, city);

        if (mallDto.getServices() != null && !mallDto.getServices().isEmpty()) {
            mallDto.getServices().forEach(serviceDto -> {
                MallServiceEntity serviceEntity = MallServiceMapper.toEntity(serviceDto);
                serviceEntity.setMall(mall);
                mall.getServices().add(serviceEntity);
            });
        }

        if (mallDto.getRestaurants() != null && !mallDto.getRestaurants().isEmpty()) {
            mallDto.getRestaurants().forEach(restaurantDto -> {
                // Validate restaurant logo if provided
                if (restaurantDto.getLogoUuid() != null) {
                    getAndValidateImage(restaurantDto.getLogoUuid());
                }
                MallRestaurant restaurantEntity = MallRestaurantMapper.toEntity(restaurantDto);
                restaurantEntity.setMall(mall);
                mall.getRestaurants().add(restaurantEntity);
            });
        }

        Mall savedMall = mallRepository.save(mall);

        log.info("Mall created: mallId={}, name={}", savedMall.getMallId(), savedMall.getName());

        // Build restaurant DTOs with logo image
        List<MallRestaurantDto> restaurantDtos = savedMall.getRestaurants().stream()
                .map(r -> MallRestaurantMapper.toFullDto(r, fetchImageSafely(r.getLogoUuid())))
                .collect(Collectors.toList());

        return MallMapper.toFullDto(savedMall, logoImage, mallImages, restaurantDtos);
    }

    @Override
    @Transactional
    public MallDto update(MallDto mallDto) {
        Mall existing = mallRepository.findById(mallDto.getMallId())
                .orElseThrow(MallExceptions::mallNotFound);

        City city = null;

        if (mallDto.getCity() != null && mallDto.getCity().getCityId() != null) {
            Long newCityId = mallDto.getCity().getCityId();
            Long currentCityId = existing.getCity().getCityId();

            if (!newCityId.equals(currentCityId)) {
                // City is actually changing
                city = cityRepository.findById(newCityId)
                        .orElseThrow(CityExceptions::cityNotFound);

                // Use the new name if provided, otherwise fall back to the existing name
                String nameToCheck = mallDto.getName() != null ? mallDto.getName() : existing.getName();

                // Case 1 & 2: check the effective name against the NEW city
                if (mallRepository.existsByNameAndCity_CityIdAndMallIdNot(
                        nameToCheck, newCityId, mallDto.getMallId())) {
                    throw MallExceptions.mallNameExistsInCity();
                }
            }
        }

        if (mallDto.getName() != null && !mallDto.getName().equals(existing.getName())) {
            Long effectiveCityId = (city != null)
                    ? city.getCityId()
                    : existing.getCity().getCityId();

            if (mallRepository.existsByNameAndCity_CityIdAndMallIdNot(
                    mallDto.getName(), effectiveCityId, mallDto.getMallId())) {
                throw MallExceptions.mallNameExistsInCity();
            }
        }

        // Validate new logo if provided
        if (mallDto.getLogoUuid() != null && !mallDto.getLogoUuid().equals(existing.getLogoUuid())) {
            getAndValidateImage(mallDto.getLogoUuid());
        }

        // Validate new mall images if provided
        if (mallDto.getMallImagesUuids() != null && !mallDto.getMallImagesUuids().isEmpty()) {
            getAndValidateImages(mallDto.getMallImagesUuids());
        }

        // If services list is provided, replace all existing services
        if (mallDto.getServices() != null) {
            existing.getServices().clear();
            mallDto.getServices().forEach(serviceDto -> {
                MallServiceEntity serviceEntity = MallServiceMapper.toEntity(serviceDto);
                serviceEntity.setMall(existing);
                existing.getServices().add(serviceEntity);
            });
        }

        // If restaurants list is provided, replace all existing restaurants
        if (mallDto.getRestaurants() != null) {
            existing.getRestaurants().clear();
            mallDto.getRestaurants().forEach(restaurantDto -> {
                // Validate restaurant logo if provided
                if (restaurantDto.getLogoUuid() != null) {
                    getAndValidateImage(restaurantDto.getLogoUuid());
                }
                MallRestaurant restaurantEntity = MallRestaurantMapper.toEntity(restaurantDto);
                restaurantEntity.setMall(existing);
                existing.getRestaurants().add(restaurantEntity);
            });
        }

        MallMapper.merge(existing, mallDto, city);
        Mall savedMall = mallRepository.save(existing);

        log.info("Mall updated: mallId={}", savedMall.getMallId());

        return toDtoWithMedia(savedMall);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Mall mall = mallRepository.findById(id)
                .orElseThrow(MallExceptions::mallNotFound);

        long activeShops = shopRepository.countByMall_MallIdAndStatus(id, ShopStatus.ACTIVE);
        if (activeShops > 0) {
            throw MallExceptions.mallHasActiveShops();
        }

        mallRepository.delete(mall);
        log.info("Mall deleted: mallId={}", id);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, MallStatus status) {
        Mall mall = mallRepository.findById(id)
                .orElseThrow(MallExceptions::mallNotFound);

        if (status == null) {
            throw MallExceptions.invalidMallStatus();
        }

        mall.setStatus(status);
        mallRepository.save(mall);
        log.info("Mall status changed: mallId={}, status={}", id, status);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        changeStatus(id, MallStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        changeStatus(id, MallStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void setMaintenance(Long id) {
        changeStatus(id, MallStatus.MAINTENANCE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> getMallsByCity(Long cityId) {
        return mallRepository.findByCity_CityId(cityId).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> getActiveMallsByCity(Long cityId) {
        return mallRepository.findByCity_CityIdAndStatus(cityId, MallStatus.ACTIVE).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> getMallsByStatus(MallStatus status) {
        return mallRepository.findByStatus(status).stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    private MallDto toDtoWithMedia(Mall mall) {
        FileDto logoImage = fetchImageSafely(mall.getLogoUuid());
        List<FileDto> mallImages = fetchImagesSafely(mall.getMallImagesUuids());

        List<MallRestaurantDto> restaurantDtos = mall.getRestaurants() != null
                ? mall.getRestaurants().stream()
                .map(r -> MallRestaurantMapper.toFullDto(r, fetchImageSafely(r.getLogoUuid())))
                .collect(Collectors.toList())
                : Collections.emptyList();

        return MallMapper.toFullDto(mall, logoImage, mallImages, restaurantDtos);
    }

    private FileDto fetchImageSafely(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        FileDto fileDto = fileService.getById(uuid);
        return fileDto;
    }

    private List<FileDto> fetchImagesSafely(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }
        return uuids.stream()
                .map(this::fetchImageSafely)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    private FileDto getAndValidateImage(UUID uuid) {
        FileDto fileDto = fileService.getById(uuid);
        if (!isImage(fileDto.getMimeType())) {
            throw MallExceptions.invalidFileType();
        }
        return fileDto;
    }

    private List<FileDto> getAndValidateImages(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }
        return uuids.stream()
                .map(this::getAndValidateImage)
                .collect(Collectors.toList());
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

}
