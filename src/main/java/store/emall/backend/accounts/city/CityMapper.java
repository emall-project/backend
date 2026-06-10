package store.emall.backend.accounts.city;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class CityMapper {

    public static CityDto toDto(City city) {
        return Optional.ofNullable(city)
                .map(c -> CityDto.builder()
                        .cityId(c.getCityId())
                        .name(c.getName())
                        .baseFee(c.getBaseFee())
                        .isActive(c.getIsActive())
                        .build())
                .orElse(null);
    }

    public static City toEntity(CityDto cityDto) {
        return Optional.ofNullable(cityDto)
                .map(dto -> City.builder()
                        .cityId(dto.getCityId())
                        .name(dto.getName())
                        .baseFee(dto.getBaseFee())
                        .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                        .build())
                .orElse(null);
    }

    public static City merge(City existing, CityDto dto) {
        if(existing == null || dto == null) {
            return existing;
        }

        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setBaseFee(firstNonNull(dto.getBaseFee(), existing.getBaseFee()));
        existing.setIsActive(firstNonNull(dto.getIsActive(), existing.getIsActive()));

        return existing;
    }
}
