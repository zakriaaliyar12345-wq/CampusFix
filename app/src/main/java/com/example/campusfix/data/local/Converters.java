package com.example.campusfix.data.local;

import androidx.room.TypeConverter;

import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;

/**
 * Room type converters: enums are persisted as their name() strings.
 * Parsing is defensive — a corrupted or future/unknown value falls back to a
 * safe default instead of crashing the app (requirement: handle malformed
 * data safely).
 */
public class Converters {

    @TypeConverter
    public static String fromStatus(ReportStatus status) {
        return status == null ? ReportStatus.SUBMITTED.name() : status.name();
    }

    @TypeConverter
    public static ReportStatus toStatus(String value) {
        return ReportStatus.from(value, ReportStatus.SUBMITTED);
    }

    @TypeConverter
    public static String fromCategory(ReportCategory category) {
        return category == null ? ReportCategory.OTHER.name() : category.name();
    }

    @TypeConverter
    public static ReportCategory toCategory(String value) {
        return ReportCategory.from(value, ReportCategory.OTHER);
    }

    @TypeConverter
    public static String fromPriority(ReportPriority priority) {
        return priority == null ? ReportPriority.MEDIUM.name() : priority.name();
    }

    @TypeConverter
    public static ReportPriority toPriority(String value) {
        return ReportPriority.from(value, ReportPriority.MEDIUM);
    }
}
