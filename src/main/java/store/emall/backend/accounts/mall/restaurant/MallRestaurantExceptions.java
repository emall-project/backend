package store.emall.backend.accounts.mall.restaurant;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class MallRestaurantExceptions {

    private MallRestaurantExceptions() {}

    public static EMallsException restaurantNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.MALL_RESTAURANT_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("restaurantId", MessageKey.MALL_RESTAURANT_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException restaurantNameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.MALL_RESTAURANT_NAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.MALL_RESTAURANT_NAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException restaurantsBelongToDifferentMalls() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.MALL_RESTAURANT_BATCH_DIFFERENT_MALLS.getKey())
                .errorCode(List.of(
                        new ErrorCode("batch", MessageKey.MALL_RESTAURANT_BATCH_DIFFERENT_MALLS.getKey())
                ))
                .build();
    }

    public static EMallsException imageNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.RESTAURANT_IMAGE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("image", MessageKey.RESTAURANT_IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException invalidFileType() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.RESTAURANT_FILE_INVALID_TYPE.getKey())
                .errorCode(List.of(
                        new ErrorCode("file", MessageKey.RESTAURANT_FILE_INVALID_TYPE.getKey())
                ))
                .build();
    }

    public static EMallsException mediaServiceUnavailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.RESTAURANT_MEDIA_SERVICE_UNAVAILABLE.getKey())
                .errorCode(List.of(
                        new ErrorCode("media", MessageKey.RESTAURANT_MEDIA_SERVICE_UNAVAILABLE.getKey())
                ))
                .build();
    }
}
