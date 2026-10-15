package com.example.campusfix.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Per-lesson progress for the Android Learning Lab: completion flag,
 * bookmark flag and best quiz score. Primary key is the lesson id
 * (a stable string such as "04.5") so rows are upserted, never duplicated.
 *
 * Included in database schema version 1 alongside reports so the schema
 * does not have to change later (no migration needed for v1 users).
 */
@Entity(tableName = "lesson_progress")
public class LessonProgressEntity {

    @PrimaryKey
    @NonNull
    public String lessonId = "";

    public boolean completed;

    public boolean bookmarked;

    /** -1 until the quiz has been attempted at least once. */
    public int bestQuizScore = -1;

    public long lastOpenedAt;

    public long completedAt;
}
