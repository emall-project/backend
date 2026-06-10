package store.emall.backend.accounts.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ShopCategory {
    CLOTHING("CLOTHING", "CLOTHING"),
    ELECTRONICS("ELECTRONICS", "ELECTRONICS"),
    FOOD("FOOD", "FOOD"),
    BEVERAGES("BEVERAGES", "BEVERAGES"),
    BOOKS("BOOKS", "BOOKS"),
    TOYS("TOYS", "TOYS"),
    JEWELRY("JEWELRY", "JEWELRY"),
    SPORTS("SPORTS", "SPORTS"),
    HEALTH("HEALTH", "HEALTH"),
    BEAUTY("BEAUTY", "BEAUTY"),
    HOME("HOME", "HOME"),
    FURNITURE("FURNITURE", "FURNITURE"),
    OTHER("OTHER", "OTHER");

    private final String value;
    @Getter
    private final String description;

    ShopCategory(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ShopCategory fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Discount type cannot be null or blank");
        }
        for (ShopCategory type : values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                String.format("Invalid discount type: '%s'. Valid options are: PERCENT, FIXED_PRICE", value)
        );
    }
}