package store.emall.backend.accounts.user;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class UserExceptions {

    private UserExceptions() {}

    public static EMallsException userNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.USER_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("userId", MessageKey.USER_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException usernameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.USERNAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("username", MessageKey.USERNAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException phoneExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.PHONE_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("phone", MessageKey.PHONE_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException emailExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.EMAIL_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("email", MessageKey.EMAIL_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException nationalIdExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.NATIONAL_ID_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("nationalIdNumber", MessageKey.NATIONAL_ID_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException imageNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.USER_IMAGE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("profilePictureUuid", MessageKey.USER_IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException invalidFileType() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.USER_FILE_INVALID_TYPE.getKey())
                .errorCode(List.of(
                        new ErrorCode("profilePictureUuid", MessageKey.USER_FILE_INVALID_TYPE.getKey())
                ))
                .build();
    }

    public static EMallsException mediaServiceUnavailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.USER_MEDIA_SERVICE_UNAVAILABLE.getKey())
                .errorCode(List.of(
                        new ErrorCode("media", MessageKey.USER_MEDIA_SERVICE_UNAVAILABLE.getKey())
                ))
                .build();
    }

    public static EMallsException invalidCurrentPassword() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.USER_INVALID_CURRENT_PASSWORD.getKey())
                .errorCode(List.of(
                        new ErrorCode("currentPassword", MessageKey.USER_INVALID_CURRENT_PASSWORD.getKey())
                ))
                .build();
    }

    public static EMallsException newPasswordSameAsCurrent() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.USER_NEW_PASSWORD_SAME_AS_CURRENT.getKey())
                .errorCode(List.of(
                        new ErrorCode("newPassword", MessageKey.USER_NEW_PASSWORD_SAME_AS_CURRENT.getKey())
                ))
                .build();
    }

    public static EMallsException unauthorizedProfileUpdate() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.USER_UNAUTHORIZED_PROFILE_UPDATE.getKey())
                .errorCode(List.of(
                        new ErrorCode("userId", MessageKey.USER_UNAUTHORIZED_PROFILE_UPDATE.getKey())
                ))
                .build();
    }

    public static EMallsException userIsProtected() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.USER_IS_PROTECTED.getKey())
                .errorCode(List.of(new ErrorCode("userId", MessageKey.USER_IS_PROTECTED.getKey())))
                .build();
    }

}
