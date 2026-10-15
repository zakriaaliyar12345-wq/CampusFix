package com.example.campusfix.data.model;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.example.campusfix.R;

/**
 * Workflow states for a campus report. Stored in Room as the enum name()
 * (see data.local.Converters) so renaming a display label never corrupts data.
 */
public enum ReportStatus {

    SUBMITTED(R.string.status_submitted, R.color.status_submitted),
    IN_PROGRESS(R.string.status_in_progress, R.color.status_in_progress),
    RESOLVED(R.string.status_resolved, R.color.status_resolved),
    REJECTED(R.string.status_rejected, R.color.status_rejected);

    @StringRes
    public final int labelRes;

    @ColorRes
    public final int colorRes;

    ReportStatus(int labelRes, int colorRes) {
        this.labelRes = labelRes;
        this.colorRes = colorRes;
    }

    /** Null/unknown-safe parsing so malformed database rows never crash the app. */
    public static ReportStatus from(String value, ReportStatus fallback) {
        if (value == null) return fallback;
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
