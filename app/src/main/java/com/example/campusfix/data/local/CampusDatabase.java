package com.example.campusfix.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.campusfix.data.local.dao.LessonProgressDao;
import com.example.campusfix.data.local.dao.ReportDao;
import com.example.campusfix.data.local.dao.StatusHistoryDao;
import com.example.campusfix.data.local.entity.LessonProgressEntity;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;

/**
 * The single Room database for CampusFix.
 *
 * Schema version 1 already contains every entity the finished app needs
 * (reports, status history, lesson progress), so v1 users never need a
 * migration. If the schema ever changes after release, add a Migration
 * object (or an exported autoMigration) — never fallbackToDestructive
 * migration, which would erase user reports.
 *
 * Database construction happens lazily on first access, inside
 * getInstance(), synchronized so concurrent screens share one instance
 * (Room is expensive to create and one instance avoids open-file issues).
 */
@Database(
        entities = {
                ReportEntity.class,
                StatusHistoryEntity.class,
                LessonProgressEntity.class
        },
        version = 1,
        exportSchema = true
)
@TypeConverters(Converters.class)
public abstract class CampusDatabase extends RoomDatabase {

    private static final String DB_NAME = "campusfix.db";

    private static volatile CampusDatabase instance;

    public abstract ReportDao reportDao();

    public abstract StatusHistoryDao statusHistoryDao();

    public abstract LessonProgressDao lessonProgressDao();

    public static CampusDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (CampusDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CampusDatabase.class,
                                    DB_NAME)
                            .build();
                }
            }
        }
        return instance;
    }

    /** Helper used by tests to build an isolated in-memory database. */
    @NonNull
    public static CampusDatabase createInMemory(Context context) {
        return Room.inMemoryDatabaseBuilder(
                        context.getApplicationContext(),
                        CampusDatabase.class)
                .build();
    }
}
