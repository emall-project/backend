package store.emall.backend.accounts.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ShopStatus {
    ACTIVE("ACTIVE", "ACTIVE Status"),
    INACTIVE("INACTIVE", "INACTIVE Status");


    private final String value;
    @Getter
    private final String description;

    ShopStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ShopStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Discount type cannot be null or blank");
        }
        for (ShopStatus type : values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                String.format("Invalid discount type: '%s'. Valid options are: PERCENT, FIXED_PRICE", value)
        );
    }


}