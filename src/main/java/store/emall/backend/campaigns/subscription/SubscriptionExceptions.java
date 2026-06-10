package store.emall.backend.campaigns.subscription;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class SubscriptionExceptions {

    private SubscriptionExceptions() {}

    public static EMallsException subscriptionNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SUBSCRIPTION_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("subscriptionId",
                        MessageKey.SUBSCRIPTION_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException shopAlreadyHasSubscription() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SUBSCRIPTION_SHOP_ALREADY_HAS_ONE.getKey())
                .errorCode(List.of(new ErrorCode("shopId",
                        MessageKey.SUBSCRIPTION_SHOP_ALREADY_HAS_ONE.getKey())))
                .build();
    }

    public static EMallsException planNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SUBSCRIPTION_PLAN_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("planId",
                        MessageKey.SUBSCRIPTION_PLAN_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException subscriptionAlreadyActive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SUBSCRIPTION_ALREADY_ACTIVE.getKey())
                .errorCode(List.of(new ErrorCode("subscriptionId",
                        MessageKey.SUBSCRIPTION_ALREADY_ACTIVE.getKey())))
                .build();
    }

    public static EMallsException subscriptionCancelled() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.SUBSCRIPTION_CANCELLED.getKey())
                .errorCode(List.of(new ErrorCode("subscriptionId",
                        MessageKey.SUBSCRIPTION_CANCELLED.getKey())))
                .build();
    }

    public static EMallsException shopNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SUBSCRIPTION_SHOP_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("shopId",
                        MessageKey.SUBSCRIPTION_SHOP_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException subscriptionWriteAccessDenied() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.SUBSCRIPTION_WRITE_ACCESS_DENIED.getKey())
                .errorCode(List.of(new ErrorCode("shopId",
                        MessageKey.SUBSCRIPTION_WRITE_ACCESS_DENIED.getKey())))
                .build();
    }

    public static EMallsException stripeError() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_GATEWAY)
                .message(MessageKey.SUBSCRIPTION_STRIPE_ERROR.getKey())
                .build();
    }

    public static EMallsException shopBlocked() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.SHOP_BLOCKED.getKey())
                .errorCode(List.of(
                        new ErrorCode("shopId", MessageKey.SHOP_BLOCKED.getKey())
                ))
                .build();
    }
}