package store.emall.backend.campaigns.offer;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "title",           path = "title",         spec = LikeIgnoreCase.class),
        @Spec(params = "status",          path = "status",        spec = Equal.class),
        @Spec(params = "discount_type",   path = "discountType",  spec = Equal.class),
        @Spec(params = "shop_id",         path = "shopId",        spec = Equal.class),
        @Spec(params = "start_date_from", path = "startDate",     spec = GreaterThanOrEqual.class),
        @Spec(params = "start_date_to",   path = "startDate",     spec = LessThanOrEqual.class),
        @Spec(params = "end_date_from",   path = "endDate",       spec = GreaterThanOrEqual.class),
        @Spec(params = "end_date_to",     path = "endDate",       spec = LessThanOrEqual.class)
})
public interface OfferSpec extends Specification<Offer> {
}