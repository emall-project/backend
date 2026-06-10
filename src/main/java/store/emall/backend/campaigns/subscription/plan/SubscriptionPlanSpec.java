package store.emall.backend.campaigns.subscription.plan;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "plan_type", path = "planType", spec = Equal.class),
        @Spec(params = "is_active", path = "isActive", spec = Equal.class)
})
public interface SubscriptionPlanSpec extends Specification<SubscriptionPlan> {}