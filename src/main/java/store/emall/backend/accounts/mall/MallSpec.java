package store.emall.backend.accounts.mall;

import jakarta.persistence.criteria.JoinType;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Join(path = "services", alias = "s", type = JoinType.LEFT, distinct = true)
@Join(path = "restaurants", alias = "r", type = JoinType.LEFT, distinct = true)
@Join(path = "city", alias = "c", type = JoinType.LEFT, distinct = true)
@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "city-name", path = "c.name", spec = LikeIgnoreCase.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "location", path = "location", spec = LikeIgnoreCase.class),
        @Spec(params = "service-name", path = "s.name", spec = LikeIgnoreCase.class),
        @Spec(params = "restaurants-name", path = "r.name", spec = LikeIgnoreCase.class),
        @Spec(params = "cuisine-type", path = "r.cuisineType", spec = LikeIgnoreCase.class)
})
public interface MallSpec extends Specification<Mall> {
}
