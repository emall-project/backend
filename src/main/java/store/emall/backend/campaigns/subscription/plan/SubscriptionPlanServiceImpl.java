package store.emall.backend.campaigns.subscription.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.campaigns.subscription.SubscriptionExceptions;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SubscriptionPlanDto> getAll(Pageable pageable, Specification<SubscriptionPlan> spec) {
        Page<SubscriptionPlanDto> page = planRepository.findAll(spec, pageable)
                .map(SubscriptionPlanMapper::toDto);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDto> getAllPlans(Specification<SubscriptionPlan> spec) {
        List<SubscriptionPlan> plans = (spec == null)
                ? planRepository.findAll()
                : planRepository.findAll(spec);
        return plans.stream().map(SubscriptionPlanMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDto> getActivePlans() {
        return planRepository.findByIsActiveTrue()
                .stream().map(SubscriptionPlanMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlanDto getById(Long planId) {
        return planRepository.findById(planId)
                .map(SubscriptionPlanMapper::toDto)
                .orElseThrow(SubscriptionExceptions::planNotFound);
    }

    @Override
    @Transactional
    public SubscriptionPlanDto create(SubscriptionPlanDto dto) {
        SubscriptionPlan plan = SubscriptionPlanMapper.toEntity(dto);
        return SubscriptionPlanMapper.toDto(planRepository.save(plan));
    }

    @Override
    @Transactional
    public SubscriptionPlanDto update(SubscriptionPlanDto dto) {
        SubscriptionPlan existing = planRepository.findById(dto.getSubscriptionPlanId())
                .orElseThrow(SubscriptionExceptions::planNotFound);
        SubscriptionPlanMapper.merge(existing, dto);
        return SubscriptionPlanMapper.toDto(planRepository.save(existing));
    }
}