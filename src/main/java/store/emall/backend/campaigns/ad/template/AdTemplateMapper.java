package store.emall.backend.campaigns.ad.template;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class AdTemplateMapper {

    private AdTemplateMapper() {}

    public static AdTemplateDto toDto(AdTemplate entity) {
        return Optional.ofNullable(entity)
                .map(e -> AdTemplateDto.builder()
                        .adTemplateId(e.getAdTemplateId())
                        .name(e.getName())
                        .description(e.getDescription())
                        .position(e.getPosition())
                        .imageRatio(e.getImageRatio())
                        .pricePerHour(e.getPricePerHour())
                        .status(e.getStatus())
                        .build())
                .orElse(null);
    }

    public static AdTemplate toEntity(AdTemplateDto dto) {
        return Optional.ofNullable(dto)
                .map(d -> AdTemplate.builder()
                        .adTemplateId(d.getAdTemplateId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .position(d.getPosition())
                        .imageRatio(d.getImageRatio())
                        .pricePerHour(d.getPricePerHour())
                        .status(d.getStatus() != null ? d.getStatus() : AdTemplateStatus.ACTIVE)
                        .build())
                .orElse(null);
    }

    public static AdTemplate merge(AdTemplate existing, AdTemplateDto dto) {
        if (existing == null || dto == null) {
            return existing;
        }

        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
        existing.setPosition(firstNonNull(dto.getPosition(), existing.getPosition()));
        existing.setImageRatio(firstNonNull(dto.getImageRatio(), existing.getImageRatio()));
        existing.setPricePerHour(firstNonNull(dto.getPricePerHour(), existing.getPricePerHour()));
        existing.setStatus(firstNonNull(dto.getStatus(), existing.getStatus()));

        return existing;
    }
}
