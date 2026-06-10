package store.emall.backend.campaigns.ad.template;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "position", path = "position", spec = LikeIgnoreCase.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
})
public interface AdTemplateSpec extends Specification<AdTemplate> {
}
