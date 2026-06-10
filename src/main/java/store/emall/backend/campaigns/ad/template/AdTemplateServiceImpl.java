package store.emall.backend.campaigns.ad.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.campaigns.ad.request.AdRequestRepository;
import store.emall.backend.campaigns.ad.request.AdRequestStatus;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.campaigns.security.SecurityContextUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdTemplateServiceImpl implements AdTemplateService {

    private final AdTemplateRepository adTemplateRepository;
    private final AdRequestRepository adRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<AdTemplateDto> getAll(Pageable pageable, Specification<AdTemplate> spec) {
        Page<AdTemplateDto> page = adTemplateRepository.findAll(spec, pageable)
                .map(AdTemplateMapper::toDto);

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdTemplateDto> getAllTemplates(Specification<AdTemplate> spec) {
        List<AdTemplate> templates = (spec == null)
                ? adTemplateRepository.findAll()
                : adTemplateRepository.findAll(spec);

        return templates.stream()
                .map(AdTemplateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdTemplateDto getById(Long id) {
        AdTemplate template = adTemplateRepository.findById(id)
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        if (!SecurityContextUtil.isAdmin()) {
            if (template.getStatus() != AdTemplateStatus.ACTIVE) {
                throw AdTemplateExceptions.templateNotFound();
            }
        }

        return AdTemplateMapper.toDto(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdTemplateDto> getByStatus(AdTemplateStatus status) {
        return adTemplateRepository.findByStatus(status)
                .stream()
                .map(AdTemplateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdTemplateDto> getByPosition(String position) {
        return adTemplateRepository.findByPosition(position)
                .stream()
                .map(AdTemplateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdTemplateDto> getActiveByPosition(String position) {
        return adTemplateRepository.findByPositionAndStatus(position, AdTemplateStatus.ACTIVE)
                .stream()
                .map(AdTemplateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByStatus(AdTemplateStatus status) {
        return adTemplateRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByPosition(String position) {
        return adTemplateRepository.countByPosition(position);
    }

    @Override
    @Transactional
    public AdTemplateDto create(AdTemplateDto dto) {
        // Validate name uniqueness per position
        if (adTemplateRepository.existsByNameAndPosition(dto.getName(), dto.getPosition())) {
            throw AdTemplateExceptions.templateNameExists();
        }

        AdTemplate template = AdTemplateMapper.toEntity(dto);
        return AdTemplateMapper.toDto(adTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public AdTemplateDto update(AdTemplateDto dto) {
        AdTemplate existing = adTemplateRepository.findById(dto.getAdTemplateId())
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        // ARCHIVED templates are immutable
        if(existing.getStatus() == AdTemplateStatus.ARCHIVED) {
            throw AdTemplateExceptions.templateArchived();
        }

        // If name or position changed, check uniqueness again (exclude current)
        String newName = dto.getName() != null ? dto.getName() : existing.getName();
        String newPosition = dto.getPosition() != null ? dto.getPosition() : existing.getPosition();

        if((dto.getName() != null || dto.getPosition() != null)
                && adTemplateRepository.existsByNameAndPositionAndAdTemplateIdNot(
                        newName, newPosition, dto.getAdTemplateId())) {
            throw AdTemplateExceptions.templateNameExists();
        }

        boolean hasApprovedRequests = adRequestRepository
                .existsByTemplate_AdTemplateIdAndStatus(existing.getAdTemplateId(), AdRequestStatus.APPROVED);

        if (hasApprovedRequests) {
            boolean hasDisallowedChanges = dto.getName() != null
                    || dto.getPosition() != null
                    || dto.getImageRatio() != null
                    || dto.getPricePerHour() != null
                    || dto.getStatus() != null;

            if (hasDisallowedChanges) {
                throw AdTemplateExceptions.templateReservedImmutable();
            }

            existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
            return AdTemplateMapper.toDto(adTemplateRepository.save(existing));
        }

        AdTemplateMapper.merge(existing, dto);
        return AdTemplateMapper.toDto(adTemplateRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AdTemplate template = adTemplateRepository.findById(id)
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        // Cannot delete if there are pending or approved requests
        boolean hasActiveRequests = adRequestRepository
                .existsByTemplate_AdTemplateIdAndStatusIn(id,
                        List.of(AdRequestStatus.PENDING, AdRequestStatus.APPROVED));

        if(hasActiveRequests) {
            throw AdTemplateExceptions.templateHasActiveRequests();
        }

        adTemplateRepository.delete(template);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, AdTemplateStatus status) {
        AdTemplate template = adTemplateRepository.findById(id)
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        if(status == null) {
            throw AdTemplateExceptions.templateNotActive();
        }

        template.setStatus(status);
        adTemplateRepository.save(template);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        AdTemplate template = adTemplateRepository.findById(id)
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        template.setStatus(AdTemplateStatus.ACTIVE);
        adTemplateRepository.save(template);
    }

    @Override
    @Transactional
    public void archive(Long id) {
        AdTemplate template = adTemplateRepository.findById(id)
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        // Cannot archive a template that has pending or approved requests
        boolean hasActiveRequests = adRequestRepository
                .existsByTemplate_AdTemplateIdAndStatusIn(id,
                        List.of(AdRequestStatus.PENDING, AdRequestStatus.APPROVED));

        if (hasActiveRequests) {
            throw AdTemplateExceptions.templateHasActiveRequests();
        }

        template.setStatus(AdTemplateStatus.ARCHIVED);
        adTemplateRepository.save(template);
    }
}