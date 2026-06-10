package store.emall.backend.accounts.user;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
    @Spec(params = "full-name", path = "fullName", spec = LikeIgnoreCase.class),
    @Spec(params = "email", path = "email", spec = LikeIgnoreCase.class),
    @Spec(params = "phone-number", path = "phoneNumber", spec = LikeIgnoreCase.class),
    @Spec(params = "role", path = "role.roleId", spec = Equal.class),
    @Spec(params = "is_active", path = "isActive", spec = Equal.class),
    @Spec(params = "username", path = "username", spec = LikeIgnoreCase.class),
    @Spec(params = "gender", path = "gender", spec = Equal.class),
    @Spec(params = "nationalIdNumber", path = "nationalIdNumber", spec = LikeIgnoreCase.class)
})
public interface UserSpec extends Specification<User> {
}
