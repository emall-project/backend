package store.emall.backend.campaigns.ad.template;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface AdTemplateService {

    PaginatedResponse<AdTemplateDto> getAll(Pageable pageable, Specification<AdTemplate> spec);

    List<AdTemplateDto> getAllTemplates(Specification<AdTemplate> spec);

    AdTemplateDto getById(Long id);

    AdTemplateDto create(AdTemplateDto dto);

    AdTemplateDto update(AdTemplateDto dto);

    void delete(Long id);

    void changeStatus(Long id, AdTemplateStatus status);

    void activate(Long id);

    void archive(Long id);

    List<AdTemplateDto> getByStatus(AdTemplateStatus status);

    List<AdTemplateDto> getByPosition(String position);

    List<AdTemplateDto> getActiveByPosition(String position);

    Long countByStatus(AdTemplateStatus status);

    Long countByPosition(String position);
}