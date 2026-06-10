package store.emall.backend.campaigns.ad.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ImageRatio {

    // Landscape ratios
    RATIO_16_9("16:9", "Widescreen landscape (video, banners)"),
    RATIO_21_9("21:9", "Ultra-wide (cinematic)"),
    RATIO_4_3("4:3", "Standard landscape (TV, tablets)"),
    RATIO_3_2("3:2", "Classic photography landscape"),
    RATIO_5_4("5:4", "Near-square landscape"),

    // Portrait ratios
    RATIO_9_16("9:16", "Vertical video (stories, reels)"),
    RATIO_2_3("2:3", "Portrait photography"),
    RATIO_3_4("3:4", "Standard portrait"),
    RATIO_4_5("4:5", "Instagram portrait"),

    // Square
    RATIO_1_1("1:1", "Square (Instagram, profile)");

    private final String displayName;
    @Getter
    private final String description;

    ImageRatio(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ImageRatio fromDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Image ratio cannot be null or blank");
        }

        String normalized = displayName.trim();
        for (ImageRatio ratio : values()) {
            if (ratio.displayName.equals(normalized)) {
                return ratio;
            }
        }

        StringBuilder validOptions = new StringBuilder();
        for (ImageRatio ratio : values()) {
            if (validOptions.length() > 0) {
                validOptions.append(", ");
            }
            validOptions.append(ratio.displayName);
        }

        throw new IllegalArgumentException(
                String.format("Invalid image ratio: '%s'. Valid options are: %s",
                        displayName, validOptions.toString())
        );
    }

    public static boolean isValid(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return false;
        }
        for (ImageRatio ratio : values()) {
            if (ratio.displayName.equals(displayName.trim())) {
                return true;
            }
        }
        return false;
    }
}
