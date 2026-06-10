package store.emall.backend.accounts.mall.service;

import store.emall.backend.accounts.mall.MallMapper;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class MallServiceMapper {

    private MallServiceMapper() {}

    public static MallServiceDto toDto(MallServiceEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> MallServiceDto.builder()
                        .serviceId(e.getServiceId())
                        .mall(e.getMall() != null ? MallMapper.toBasicDto(e.getMall()): null)
                        .name(e.getName())
                        .description(e.getDescription())
                        .isActive(e.getIsActive())
                        .build())
                .orElse(null);
    }

    public static MallServiceEntity toEntity(MallServiceDto dto) {
        return Optional.ofNullable(dto)
                .map(d -> MallServiceEntity.builder()
                        .serviceId(d.getServiceId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                        .build())
                .orElse(null);
    }

    public static MallServiceEntity merge(MallServiceEntity existing, MallServiceDto dto) {
        if (existing == null || dto == null) {
            return existing;
        }

        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
        existing.setIsActive(firstNonNull(dto.getIsActive(), existing.getIsActive()));

        return existing;
    }
}
