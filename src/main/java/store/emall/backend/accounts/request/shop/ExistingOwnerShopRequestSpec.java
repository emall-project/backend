package store.emall.backend.accounts.request.shop;

import jakarta.persistence.criteria.JoinType;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Join(path = "existingUser", alias = "u", type = JoinType.LEFT)
@Join(path = "mall", alias = "m", type = JoinType.LEFT)
@And({
        @Spec(params = "username", path = "u.username", spec = LikeIgnoreCase.class),
        @Spec(params = "status",   path = "status",     spec = Equal.class),
        @Spec(params = "mallId",   path = "m.mallId",   spec = Equal.class),
        @Spec(params = "name",     path = "name",       spec = LikeIgnoreCase.class),
        @Spec(params = "category", path = "category",   spec = Equal.class)
})
public interface ExistingOwnerShopRequestSpec extends Specification<ShopRequest> {}