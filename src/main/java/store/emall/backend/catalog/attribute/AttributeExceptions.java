package store.emall.backend.catalog.attribute;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class AttributeExceptions {

    private AttributeExceptions() {}

    // ----------------------------
    // Not Found Exceptions
    // ----------------------------
    public static EMallsException attributeNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.ATTRIBUTE_NOT_FOUND.getKey())
                .build();
    }

    // ----------------------------
    // Bad Request Exceptions
    // ----------------------------
    public static EMallsException attributeInactive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.ATTRIBUTE_INACTIVE.getKey())
                .build();
    }

    public static EMallsException attributeHasProducts() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.ATTRIBUTE_HAS_PRODUCTS.getKey())
                .build();
    }

    // ----------------------------
    // Conflict Exceptions
    // ----------------------------
    public static EMallsException slugExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.ATTRIBUTE_SLUG_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode("slug", MessageKey.ATTRIBUTE_SLUG_EXISTS.getKey())))
                .build();
    }
}