package store.emall.backend.catalog.favorite;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.response.ErrorCode;
import store.emall.backend.common.message.MessageKey;

import java.util.List;

public final class FavoriteExceptions {

    private FavoriteExceptions() {
    }

    public static EMallsException favoriteNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.FAVORITE_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException productNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.FAVORITE_PRODUCT_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("productId", MessageKey.FAVORITE_PRODUCT_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException alreadyFavorited() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.FAVORITE_ALREADY_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode("productId", MessageKey.FAVORITE_ALREADY_EXISTS.getKey())))
                .build();
    }

    public static EMallsException favoriteAccessDenied() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.FAVORITE_ACCESS_DENIED.getKey())
                .build();
    }
}