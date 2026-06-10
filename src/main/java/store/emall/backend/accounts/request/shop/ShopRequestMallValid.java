package store.emall.backend.accounts.request.shop;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ShopRequestMallValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShopRequestMallValid {

    String message() default "shopRequest.mall.invalid";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}