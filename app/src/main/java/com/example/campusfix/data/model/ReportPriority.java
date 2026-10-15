package com.example.campusfix.data.model;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.example.campusfix.R;

/**
 * Priority levels. The sortOrder value drives the "sort by priority" option
 * (lower = more urgent).
 */
public enum ReportPriority {

    LOW(R.string.priority_low, R.color.priority_low, 3),
    MEDIUM(R.string.priority_medium, R.color.priority_medium, 2),
    HIGH(R.string.priority_high, R.color.priority_high, 1),
    CRITICAL(R.string.priority_critical, R.color.priority_critical, 0);

    @StringRes
    public final int labelRes;

    @ColorRes
    public final int colorRes;

    public final int sortOrder;

    ReportPriority(int labelRes, int colorRes, int sortOrder) {
        this.labelRes = labelRes;
        this.colorRes = colorRes;
        this.sortOrder = sortOrder;
    }

    public static ReportPriority from(String value, ReportPriority fallback) {
        if (value == null) return fallback;
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
