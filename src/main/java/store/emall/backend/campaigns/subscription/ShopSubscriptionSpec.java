package store.emall.backend.campaigns.subscription;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "shop_id", path = "shopId", spec = Equal.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "auto_renew", path = "autoRenew", spec = Equal.class),
        @Spec(params = "start_date_from", path = "startDate", spec = GreaterThanOrEqual.class),
        @Spec(params = "start_date_to", path = "startDate", spec = LessThanOrEqual.class),
        @Spec(params = "end_date_from", path = "endDate", spec = GreaterThanOrEqual.class),
        @Spec(params = "end_date_to", path = "endDate", spec = LessThanOrEqual.class),
        @Spec(params = "trial_end_from", path = "trialEndDate", spec = GreaterThanOrEqual.class),
        @Spec(params = "trial_end_to", path = "trialEndDate", spec = LessThanOrEqual.class)
})
public interface ShopSubscriptionSpec extends Specification<ShopSubscription> {}