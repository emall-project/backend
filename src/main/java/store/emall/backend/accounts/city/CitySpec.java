package store.emall.backend.accounts.city;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;


@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "is_active", path = "isActive", spec = Equal.class),
        @Spec(params = "base-fee", path = "baseFee", spec = Equal.class),
})
public interface CitySpec extends Specification<City> {
}
