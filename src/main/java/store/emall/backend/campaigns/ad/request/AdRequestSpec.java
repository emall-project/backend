package store.emall.backend.campaigns.ad.request;

import jakarta.persistence.criteria.JoinType;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.JoinFetch;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@JoinFetch(paths = {"template"}, joinType = JoinType.LEFT)
@And({
        @Spec(params = "title", path = "title", spec = LikeIgnoreCase.class),
        @Spec(params = "shop_id", path = "shopId", spec = Equal.class),
        @Spec(params = "template_id", path = "template.adTemplateId", spec = Equal.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "payment_status", path = "paymentStatus", spec = Equal.class),
        @Spec(params = "is_displayed", path = "isDisplayed", spec = Equal.class),
        @Spec(params = "start_date_from", path = "startDate", spec = GreaterThanOrEqual.class),
        @Spec(params = "start_date_to", path = "startDate", spec = LessThanOrEqual.class),
        @Spec(params = "end_date_from", path = "endDate", spec = GreaterThanOrEqual.class),
        @Spec(params = "end_date_to", path = "endDate", spec = LessThanOrEqual.class)
})
public interface AdRequestSpec extends Specification<AdRequest> {
}
