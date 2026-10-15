package com.example.campusfix.util;

import java.text.DateFormat;
import java.util.Date;

/** Small date helpers shared across screens (locale-aware, offline). */
public final class DateUtils {

    private DateUtils() {
    }

    public static String formatDate(DateFormat format, long epochMillis) {
        if (epochMillis <= 0) return "";
        return format.format(new Date(epochMillis));
    }

    public static String formatDateTime(DateFormat format, long epochMillis) {
        if (epochMillis <= 0) return "";
        return format.format(new Date(epochMillis));
    }
}
