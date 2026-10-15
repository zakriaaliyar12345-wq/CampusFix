package com.example.campusfix.data.model;

import androidx.annotation.StringRes;

import com.example.campusfix.R;

/** Problem categories a student can pick when filing a report. */
public enum ReportCategory {

    CLASSROOM_EQUIPMENT(R.string.category_classroom_equipment),
    FURNITURE(R.string.category_furniture),
    WIFI_NETWORK(R.string.category_wifi),
    PROJECTOR_AV(R.string.category_projector),
    CLEANLINESS(R.string.category_cleanliness),
    ELECTRICAL(R.string.category_electrical),
    OTHER(R.string.category_other);

    @StringRes
    public final int labelRes;

    ReportCategory(int labelRes) {
        this.labelRes = labelRes;
    }

    public static ReportCategory from(String value, ReportCategory fallback) {
        if (value == null) return fallback;
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
