package com.example.campusfix.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;

/**
 * One campus problem report. All structured data lives here (Room/SQLite
 * rather than SharedPreferences — see Learning Lab topic 09.1/10.1).
 */
@Entity(tableName = "reports")
public class ReportEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String title = "";

    @NonNull
    public ReportCategory category = ReportCategory.OTHER;

    @NonNull
    public String description = "";

    /** Free-text place, e.g. "Building B, Room 214". */
    @NonNull
    public String location = "";

    @NonNull
    public ReportPriority priority = ReportPriority.MEDIUM;

    @NonNull
    public ReportStatus status = ReportStatus.SUBMITTED;

    /** Epoch millis — sortable, locale-independent. */
    public long createdAt;

    /** Epoch millis of the last change (including status updates). */
    public long updatedAt;

    /**
     * true for rows seeded by SampleDataSeeder. Sample rows are clearly
     * labelled in the UI and never presented as real campus data.
     */
    public boolean isSample;

    /** Optional photo: a content:// URI string, or null when no photo. */
    @Nullable
    public String photoUri;
}
