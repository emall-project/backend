package store.emall.backend.campaigns.subscription.plan;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface SubscriptionPlanService {

    PaginatedResponse<SubscriptionPlanDto> getAll(Pageable pageable, Specification<SubscriptionPlan> spec);

    List<SubscriptionPlanDto> getAllPlans(Specification<SubscriptionPlan> spec);

    List<SubscriptionPlanDto> getActivePlans();

    SubscriptionPlanDto getById(Long planId);

    SubscriptionPlanDto create(SubscriptionPlanDto dto);

    SubscriptionPlanDto update(SubscriptionPlanDto dto);
}