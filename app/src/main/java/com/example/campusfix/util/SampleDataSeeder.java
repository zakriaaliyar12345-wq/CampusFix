package com.example.campusfix.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.campusfix.data.local.CampusDatabase;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;
import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;

import java.util.concurrent.TimeUnit;

/**
 * Seeds a few clearly-labelled sample reports on first launch so the list,
 * filters and statistics screens have honest demonstration data instead of
 * pretending real campus reports exist.
 *
 * Runs once (SharedPreferences guard) on the disk executor — never the main
 * thread — and every seeded row has isSample = true, which the UI shows as a
 * "SAMPLE" badge.
 */
public final class SampleDataSeeder {

    private static final String PREF_NAME = "campusfix_seed";
    private static final String KEY_SEEDED = "sample_seeded";

    private SampleDataSeeder() {
    }

    public static void seedIfNeeded(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_SEEDED, false)) {
            return;
        }

        Context appContext = context.getApplicationContext();
        AppExecutors.diskIO().execute(() -> {
            CampusDatabase db = CampusDatabase.getInstance(appContext);

            // Another path may have seeded already; keep it idempotent.
            Integer total = null;
            try {
                total = db.reportDao().getTotalCountSync();
            } catch (Exception ignored) {
                // fall through to seeding
            }
            if (total != null && total > 0) {
                prefs.edit().putBoolean(KEY_SEEDED, true).apply();
                return;
            }

            long now = System.currentTimeMillis();
            long day = TimeUnit.DAYS.toMillis(1);

            insert(db, now - 3 * day, "Projector won't turn on",
                    ReportCategory.PROJECTOR_AV,
                    "The ceiling projector in lecture hall A only shows a "
                            + "blue power light and never reaches the welcome "
                            + "screen. The remote seems to work because the "
                            + "menu button beeps.",
                    "Lecture Hall A, Ground floor",
                    ReportPriority.HIGH, ReportStatus.IN_PROGRESS);

            insert(db, now - 5 * day, "Broken chair next to the window",
                    ReportCategory.FURNITURE,
                    "One of the study-area chairs has a loose backrest that "
                            + "tilts all the way back. It is the third chair "
                            + "from the door on the second floor.",
                    "Library, 2nd floor study area",
                    ReportPriority.MEDIUM, ReportStatus.SUBMITTED);

            insert(db, now - 8 * day, "Wi-Fi drops in lab 3",
                    ReportCategory.WIFI_NETWORK,
                    "The student Wi-Fi disconnects every few minutes on the "
                            + "left side of lab 3. Other rooms in the same "
                            + "building work fine on the same account.",
                    "Science block, Lab 3",
                    ReportPriority.CRITICAL, ReportStatus.RESOLVED);

            prefs.edit().putBoolean(KEY_SEEDED, true).apply();
        });
    }

    private static void insert(CampusDatabase db,
                               long createdAt,
                               String title,
                               ReportCategory category,
                               String description,
                               String location,
                               ReportPriority priority,
                               ReportStatus status) {

        ReportEntity report = new ReportEntity();
        report.title = title;
        report.category = category;
        report.description = description;
        report.location = location;
        report.priority = priority;
        report.status = status;
        report.createdAt = createdAt;
        report.updatedAt = createdAt;
        report.isSample = true;

        long id = db.reportDao().insert(report);

        StatusHistoryEntity created = new StatusHistoryEntity();
        created.reportId = id;
        created.status = ReportStatus.SUBMITTED;
        created.timestamp = createdAt;
        created.note = "Sample report created";
        db.statusHistoryDao().insert(created);

        if (status != ReportStatus.SUBMITTED) {
            StatusHistoryEntity advanced = new StatusHistoryEntity();
            advanced.reportId = id;
            advanced.status = status;
            advanced.timestamp = createdAt + TimeUnit.HOURS.toMillis(6);
            advanced.note = "Sample status update";
            db.statusHistoryDao().insert(advanced);
        }
    }
}
