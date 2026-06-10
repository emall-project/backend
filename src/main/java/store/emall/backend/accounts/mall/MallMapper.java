package store.emall.backend.accounts.mall;

import store.emall.backend.accounts.city.City;
import store.emall.backend.accounts.city.CityMapper;
import store.emall.backend.accounts.mall.dtos.MallBasicDto;
import store.emall.backend.accounts.mall.dtos.MallDto;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantDto;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantMapper;
import store.emall.backend.accounts.mall.service.MallServiceMapper;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class MallMapper {

    private MallMapper() {}

    public static MallDto toFullDto(Mall mall,
                                    FileDto logoImage,
                                    List<FileDto> mallImages,
                                    List<MallRestaurantDto> restaurantDtos) {
        return Optional.ofNullable(mall)
                .map(m -> MallDto.builder()
                        .mallId(m.getMallId())
                        .city(CityMapper.toDto(m.getCity()))
                        .name(m.getName())
                        .description(m.getDescription())
                        .capacity(m.getCapacity())
                        .location(m.getLocation())
                        .contactInfo(m.getContactInfo())
                        .logoUuid(m.getLogoUuid())
                        .logoImage(logoImage)
                        .mallImagesUuids(m.getMallImagesUuids())
                        .mallImages(mallImages)
                        .status(m.getStatus())
                        .services(m.getServices() != null
                                ? m.getServices().stream()
                                .map(MallServiceMapper::toDto)
                                .collect(Collectors.toList())
                                : Collections.emptyList())
                        .restaurants(restaurantDtos != null
                                ? restaurantDtos
                                : Collections.emptyList())
                        .build())
                .orElse(null);
    }

    public static MallDto toDto(Mall mall) {
        return Optional.ofNullable(mall)
                .map(m -> MallDto.builder()
                        .mallId(m.getMallId())
                        .city(CityMapper.toDto(m.getCity()))
                        .name(m.getName())
                        .description(m.getDescription())
                        .capacity(m.getCapacity())
                        .location(m.getLocation())
                        .contactInfo(m.getContactInfo())
                        .logoUuid(m.getLogoUuid())
                        .mallImagesUuids(m.getMallImagesUuids())
                        .status(m.getStatus())
                        .services(m.getServices() != null
                                ? m.getServices().stream()
                                .map(MallServiceMapper::toDto)
                                .collect(Collectors.toList())
                                : Collections.emptyList())
                        .restaurants(m.getRestaurants() != null
                                ? m.getRestaurants().stream()
                                .map(MallRestaurantMapper::toDto)
                                .collect(Collectors.toList())
                                : Collections.emptyList())
                        .build())
                .orElse(null);
    }

    public static MallBasicDto toBasicDto(Mall mall) {
        return Optional.ofNullable(mall)
                .map(m -> MallBasicDto.builder()
                        .mallId(m.getMallId())
                        .name(m.getName())
                        .status(m.getStatus())
                        .build())
                .orElse(null);
    }

    public static Mall toEntity(MallDto dto, City city) {
        return Optional.ofNullable(dto)
                .map(d -> Mall.builder()
                        .mallId(d.getMallId())
                        .city(city)
                        .name(d.getName())
                        .description(d.getDescription())
                        .capacity(d.getCapacity())
                        .location(d.getLocation())
                        .contactInfo(d.getContactInfo())
                        .logoUuid(d.getLogoUuid())
                        .mallImagesUuids(d.getMallImagesUuids() != null ? d.getMallImagesUuids() : new ArrayList<>())
                        .status(d.getStatus() != null ? d.getStatus() : MallStatus.ACTIVE)
                        .build())
                .orElse(null);
    }

    public static Mall merge(Mall existing, MallDto dto, City city) {
        if (existing == null || dto == null) {
            return existing;
        }

        if (city != null) {
            existing.setCity(city);
        }

        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
        existing.setCapacity(firstNonNull(dto.getCapacity(), existing.getCapacity()));
        existing.setLocation(firstNonNull(dto.getLocation(), existing.getLocation()));

        if (dto.getContactInfo() != null) {
            existing.setContactInfo(dto.getContactInfo());
        }

        existing.setLogoUuid(firstNonNull(dto.getLogoUuid(), existing.getLogoUuid()));

        if (dto.getMallImagesUuids() != null) {
            existing.setMallImagesUuids(dto.getMallImagesUuids());
        }

        existing.setStatus(firstNonNull(dto.getStatus(), existing.getStatus()));

        return existing;
    }
}
