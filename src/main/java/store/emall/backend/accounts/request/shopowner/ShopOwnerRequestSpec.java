package store.emall.backend.accounts.request.shopowner;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "username", path = "username", spec = LikeIgnoreCase.class),
        @Spec(params = "email", path = "email", spec = LikeIgnoreCase.class),
        @Spec(params = "phone", path = "phoneNumber", spec = LikeIgnoreCase.class),
        @Spec(params = "status", path = "status", spec = Equal.class)
})
public interface ShopOwnerRequestSpec extends Specification<ShopOwnerRequest> {}