package com.example.campusfix.util;

import androidx.annotation.Nullable;

/**
 * Pure input-validation logic (no Android dependencies) so it can be tested
 * with plain JVM unit tests — see ValidatorTest.
 *
 * Each method returns null when the value is valid, or a user-facing
 * message resource id when it is not.
 */
public final class Validator {

    public static final int TITLE_MIN = 4;
    public static final int TITLE_MAX = 80;
    public static final int DESCRIPTION_MIN = 10;
    public static final int DESCRIPTION_MAX = 2000;
    public static final int LOCATION_MAX = 120;

    private Validator() {
    }

    @Nullable
    public static String validateTitle(@Nullable String title) {
        if (title == null || title.trim().isEmpty()) {
            return "error_title_required";
        }
        String trimmed = title.trim();
        if (trimmed.length() < TITLE_MIN) {
            return "error_title_too_short";
        }
        if (trimmed.length() > TITLE_MAX) {
            return "error_title_too_long";
        }
        return null;
    }

    @Nullable
    public static String validateDescription(@Nullable String description) {
        if (description == null || description.trim().isEmpty()) {
            return "error_description_required";
        }
        String trimmed = description.trim();
        if (trimmed.length() < DESCRIPTION_MIN) {
            return "error_description_too_short";
        }
        if (trimmed.length() > DESCRIPTION_MAX) {
            return "error_description_too_long";
        }
        return null;
    }

    @Nullable
    public static String validateLocation(@Nullable String location) {
        if (location == null || location.trim().isEmpty()) {
            return "error_location_required";
        }
        if (location.trim().length() > LOCATION_MAX) {
            return "error_location_too_long";
        }
        return null;
    }

    /**
     * Full-form validation used by the create/edit screen.
     *
     * @return null when every field is valid, otherwise the message key of
     * the first invalid field.
     */
    @Nullable
    public static String validateReport(@Nullable String title,
                                        @Nullable String description,
                                        @Nullable String location) {
        String titleError = validateTitle(title);
        if (titleError != null) return titleError;

        String descriptionError = validateDescription(description);
        if (descriptionError != null) return descriptionError;

        return validateLocation(location);
    }

    /** Maps a validation key returned above to a real string resource id. */
    public static int errorResOf(String key) {
        if ("error_title_required".equals(key)) {
            return com.example.campusfix.R.string.error_title_required;
        } else if ("error_title_too_short".equals(key)) {
            return com.example.campusfix.R.string.error_title_too_short;
        } else if ("error_title_too_long".equals(key)) {
            return com.example.campusfix.R.string.error_title_too_long;
        } else if ("error_description_required".equals(key)) {
            return com.example.campusfix.R.string.error_description_required;
        } else if ("error_description_too_short".equals(key)) {
            return com.example.campusfix.R.string.error_description_too_short;
        } else if ("error_description_too_long".equals(key)) {
            return com.example.campusfix.R.string.error_description_too_long;
        } else if ("error_location_required".equals(key)) {
            return com.example.campusfix.R.string.error_location_required;
        } else if ("error_location_too_long".equals(key)) {
            return com.example.campusfix.R.string.error_location_too_long;
        }
        return com.example.campusfix.R.string.state_error;
    }
}
