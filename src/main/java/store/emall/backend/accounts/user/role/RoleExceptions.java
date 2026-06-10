package store.emall.backend.accounts.user.role;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class RoleExceptions {

    private RoleExceptions() {}

    public static EMallsException roleNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.ROLE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("role", MessageKey.ROLE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException roleExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.ROLE_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("role", MessageKey.ROLE_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException roleHasUsers() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.ROLE_HAS_USERS.getKey())
                .errorCode(List.of(
                        new ErrorCode("role", MessageKey.ROLE_HAS_USERS.getKey())
                ))
                .build();
    }
}
