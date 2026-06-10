package store.emall.backend.campaigns.subscription.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import store.emall.backend.campaigns.subscription.SubscriptionPlanType;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long>,
        JpaSpecificationExecutor<SubscriptionPlan> {

    List<SubscriptionPlan> findByIsActiveTrue();

    Optional<SubscriptionPlan> findByPlanTypeAndIsActiveTrue(SubscriptionPlanType planType);
}