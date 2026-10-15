package com.example.campusfix.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.campusfix.data.local.dao.LessonProgressDao;
import com.example.campusfix.data.local.entity.LessonProgressEntity;
import com.example.campusfix.util.AppExecutors;

import java.util.List;

/**
 * Lesson-progress store for the Learning Lab (topic 10.1: repository in
 * front of the DAO; topic 09.1 contrast: this structured, evolving data
 * belongs in Room, not SharedPreferences).
 *
 * Every write runs on the disk executor; reads are LiveData delivered on the
 * main thread. Rows are keyed by lesson id and upserted, never duplicated.
 */
public class LessonProgressRepository {

    /** Passing score: 60% of the quiz marks the lesson complete. */
    public static final double PASS_RATIO = 0.6;

    private final LessonProgressDao dao;

    public LessonProgressRepository(@NonNull LessonProgressDao dao) {
        this.dao = dao;
    }

    // ----------------------------------------------------------------- reads

    public LiveData<LessonProgressEntity> getProgress(@NonNull String lessonId) {
        return dao.getById(lessonId);
    }

    public LiveData<List<LessonProgressEntity>> getAll() {
        return dao.getAll();
    }

    public LiveData<List<LessonProgressEntity>> getBookmarked() {
        return dao.getBookmarked();
    }

    public LiveData<Integer> getCompletedCount() {
        return dao.getCompletedCount();
    }

    public LiveData<Integer> getBookmarkedCount() {
        return dao.getBookmarkedCount();
    }

    // ---------------------------------------------------------------- writes

    /** Flips the bookmark flag (creating the row on first use). */
    public void toggleBookmark(@NonNull String lessonId) {
        AppExecutors.diskIO().execute(() -> {
            LessonProgressEntity entity = loadOrNew(lessonId);
            entity.bookmarked = !entity.bookmarked;
            dao.upsert(entity);
        });
    }

    /** Marks or unmarks a lesson as completed. */
    public void setCompleted(@NonNull String lessonId, boolean completed) {
        AppExecutors.diskIO().execute(() -> {
            LessonProgressEntity entity = loadOrNew(lessonId);
            entity.completed = completed;
            entity.completedAt = completed
                    ? System.currentTimeMillis() : 0L;
            dao.upsert(entity);
        });
    }

    /**
     * Records a quiz attempt: keeps the best score, touches lastOpenedAt and
     * marks the lesson complete when the score reaches {@link #PASS_RATIO}.
     *
     * @return true when this attempt is a pass.
     */
    public boolean recordQuizScore(@NonNull String lessonId,
                                   int score, int total) {
        final boolean pass = total > 0
                && (double) score / total >= PASS_RATIO;
        AppExecutors.diskIO().execute(() -> {
            LessonProgressEntity entity = loadOrNew(lessonId);
            if (score > entity.bestQuizScore) {
                entity.bestQuizScore = score;
            }
            if (pass) {
                entity.completed = true;
                entity.completedAt = System.currentTimeMillis();
            }
            dao.upsert(entity);
        });
        return pass;
    }

    /** Marks "last opened" when the reader views a lesson. */
    public void touch(@NonNull String lessonId) {
        AppExecutors.diskIO().execute(() -> {
            LessonProgressEntity entity = loadOrNew(lessonId);
            entity.lastOpenedAt = System.currentTimeMillis();
            dao.upsert(entity);
        });
    }

    @NonNull
    private LessonProgressEntity loadOrNew(@NonNull String lessonId) {
        LessonProgressEntity existing = dao.getByIdSync(lessonId);
        if (existing != null) return existing;
        LessonProgressEntity fresh = new LessonProgressEntity();
        fresh.lessonId = lessonId;
        return fresh;
    }

    /** Synchronous helper for tests (never call on the main thread). */
    @Nullable
    public LessonProgressEntity getProgressSync(@NonNull String lessonId) {
        return dao.getByIdSync(lessonId);
    }
}
