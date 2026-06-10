package store.emall.backend.accounts.request.shopowner;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class ShopOwnerRequestExceptions {

    private ShopOwnerRequestExceptions() {}

    public static EMallsException requestNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SHOP_OWNER_REQUEST_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("id", MessageKey.SHOP_OWNER_REQUEST_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException requestAlreadyProcessed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_OWNER_REQUEST_ALREADY_PROCESSED.getKey())
                .errorCode(List.of(new ErrorCode("status", MessageKey.SHOP_OWNER_REQUEST_ALREADY_PROCESSED.getKey())))
                .build();
    }

    public static EMallsException rejectionReasonRequired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.SHOP_OWNER_REQUEST_REJECTION_REASON_REQUIRED.getKey())
                .errorCode(List.of(new ErrorCode("rejectionReason", MessageKey.SHOP_OWNER_REQUEST_REJECTION_REASON_REQUIRED.getKey())))
                .build();
    }

    public static EMallsException missingShopRequest() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                .message(MessageKey.SHOP_OWNER_REQUEST_MISSING_SHOP_REQUEST.getKey())
                .errorCode(List.of(new ErrorCode("shopRequest", MessageKey.SHOP_OWNER_REQUEST_MISSING_SHOP_REQUEST.getKey())))
                .build();
    }

    public static EMallsException usernameAlreadyPendingOrExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_OWNER_REQUEST_USERNAME_PENDING.getKey())
                .errorCode(List.of(new ErrorCode("username", MessageKey.SHOP_OWNER_REQUEST_USERNAME_PENDING.getKey())))
                .build();
    }

    public static EMallsException phoneAlreadyPendingOrExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_OWNER_REQUEST_PHONE_PENDING.getKey())
                .errorCode(List.of(new ErrorCode("phone", MessageKey.SHOP_OWNER_REQUEST_PHONE_PENDING.getKey())))
                .build();
    }

    public static EMallsException emailAlreadyPendingOrExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_OWNER_REQUEST_EMAIL_PENDING.getKey())
                .errorCode(List.of(new ErrorCode("email", MessageKey.SHOP_OWNER_REQUEST_EMAIL_PENDING.getKey())))
                .build();
    }

    public static EMallsException shopNameAlreadyRequestedInMall() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_REQUEST_NAME_EXISTS_IN_MALL.getKey())
                .errorCode(List.of(new ErrorCode("shopRequest.name", MessageKey.SHOP_REQUEST_NAME_EXISTS_IN_MALL.getKey())))
                .build();
    }

    public static EMallsException mediaServiceUnavailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.MEDIA_SERVICE_UNAVAILABLE.getKey())
                .errorCode(List.of(new ErrorCode("mediaService", MessageKey.MEDIA_SERVICE_UNAVAILABLE.getKey())))
                .build();
    }

    public static EMallsException invalidFileType(String fieldName) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.INVALID_FILE_TYPE.getKey())
                .errorCode(List.of(new ErrorCode(fieldName, MessageKey.INVALID_FILE_TYPE.getKey())))
                .build();
    }

    public static EMallsException imageNotFound(String fieldName) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.MEDIA_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode(fieldName, MessageKey.MEDIA_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException imageNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.MEDIA_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("file", MessageKey.MEDIA_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException nationalIdAlreadyPendingOrExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_OWNER_REQUEST_NATIONAL_ID_EXISTS_OR_PENDING.getKey())
                .errorCode(List.of(new ErrorCode("nationalIdNumber", MessageKey.SHOP_OWNER_REQUEST_NATIONAL_ID_EXISTS_OR_PENDING.getKey())))
                .build();
    }

    public static EMallsException invalidCredentials() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(MessageKey.INVALID_CREDENTIALS.getKey())
                .errorCode(List.of(new ErrorCode("credentials", MessageKey.INVALID_CREDENTIALS.getKey())))
                .build();
    }

    public static EMallsException notAShopOwner() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.NOT_A_SHOP_OWNER.getKey())
                .errorCode(List.of(new ErrorCode("role", MessageKey.NOT_A_SHOP_OWNER.getKey())))
                .build();
    }

    public static EMallsException shopRequestRequired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.SHOP_OWNER_REQUEST_MISSING_SHOP_REQUEST.getKey())
                .errorCode(List.of(new ErrorCode("shopRequest", MessageKey.SHOP_OWNER_REQUEST_MISSING_SHOP_REQUEST.getKey())))
                .build();
    }

    public static EMallsException wrongRequestType() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.WRONG_REQUEST_TYPE.getKey())
                .errorCode(List.of(new ErrorCode("requestType", MessageKey.WRONG_REQUEST_TYPE.getKey())))
                .build();
    }

    public static EMallsException requestedMallCityNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SHOP_REQUEST_MALL_CITY_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode(
                        "requestedMallCityId",
                        MessageKey.SHOP_REQUEST_MALL_CITY_NOT_FOUND.getKey())))
                .build();
    }


    public static EMallsException requestedMallNameAlreadyExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_REQUEST_REQUESTED_MALL_NAME_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode(
                        "requestedMallName",
                        MessageKey.SHOP_REQUEST_REQUESTED_MALL_NAME_EXISTS.getKey())))
                .build();
    }
}