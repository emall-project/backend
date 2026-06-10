package store.emall.backend.catalog.attribute.attribute_options;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class AttributeOptionsExceptions {

    private AttributeOptionsExceptions() {}

    public static EMallsException duplicationInOrderSort() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.DUPLICATION_IN_ORDER_SORT.getKey())
                .build();
    }
    public static EMallsException optionNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.ATTRIBUTE_OPTION_NO_FOUND.getKey())
                .build();
    }
    public static EMallsException duplicationInValue() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.DUPLICATION_IN_VALUE.getKey())
                .build();
    }


}
