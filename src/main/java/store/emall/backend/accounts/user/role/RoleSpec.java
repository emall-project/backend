package store.emall.backend.accounts.user.role;

import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "code", path = "code", spec = LikeIgnoreCase.class),
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class)
})
public interface RoleSpec extends Specification<Role> {
}
