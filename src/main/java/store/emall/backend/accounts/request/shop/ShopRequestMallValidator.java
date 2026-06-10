package store.emall.backend.accounts.request.shop;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ShopRequestMallValidator
        implements ConstraintValidator<ShopRequestMallValid, ShopRequestDto> {

    @Override
    public boolean isValid(ShopRequestDto dto, ConstraintValidatorContext ctx) {

        boolean hasExistingMall = dto.getMallId() != null;
        boolean hasNewMallRequest = dto.getRequestedMallName() != null
                && !dto.getRequestedMallName().isBlank()
                && dto.getRequestedMallCityId() != null;

        if (hasExistingMall == hasNewMallRequest) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("shopRequest.mall.invalid")
                    .addPropertyNode("mallId")
                    .addConstraintViolation();
            return false;
        }

        if (hasNewMallRequest && dto.getRequestedMallName().length() < 2) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("shopRequest.requestedMallName.size")
                    .addPropertyNode("requestedMallName")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}