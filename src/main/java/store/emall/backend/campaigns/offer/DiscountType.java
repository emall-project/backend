package store.emall.backend.campaigns.offer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum DiscountType {

    PERCENT("PERCENT", "Percentage discount off the original price (e.g. 20%)"),
    FIXED_PRICE("FIXED_PRICE", "Fixed amount deducted from the original price (e.g. 10.00 ILS)");

    private final String value;
    @Getter
    private final String description;

    DiscountType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DiscountType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Discount type cannot be null or blank");
        }
        for (DiscountType type : values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                String.format("Invalid discount type: '%s'. Valid options are: PERCENT, FIXED_PRICE", value)
        );
    }
}