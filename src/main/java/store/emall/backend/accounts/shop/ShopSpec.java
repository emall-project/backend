package store.emall.backend.accounts.shop;

import jakarta.persistence.criteria.JoinType;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Join(path = "mall", alias = "m", type = JoinType.LEFT)
@Join(path = "owner", alias = "o", type = JoinType.LEFT)
@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "mall_id", path = "m.mallId", spec = Equal.class),
        @Spec(params = "owner_id", path = "o.userId", spec = Equal.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "category", path = "category", spec = Equal.class),
        @Spec(params = "location", path = "location", spec = LikeIgnoreCase.class)
})
public interface ShopSpec extends Specification<Shop> {
}