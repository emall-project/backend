package store.emall.backend.catalog.tag;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;

public class TagExceptions {

    public static EMallsException tagNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.TAG_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException nameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.TAG_NAME_EXIST.getKey())
                .build();
    }

    public static EMallsException tagHasProducts() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.TAG_HAS_PRODUCTS.getKey())
                .build();
    }

}
