package com.example.campusfix.ui.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.campusfix.data.local.entity.LessonProgressEntity;
import com.example.campusfix.learning.Lesson;

/**
 * One row of the Learning Lab list: either a unit header or a lesson with
 * its persisted progress flags. The stable {@link #key} doubles as the
 * DiffUtil identity so a completed lesson rebinds in place (topic 04.5).
 */
public final class LessonRow {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LESSON = 1;

    public final int type;

    /** Stable identity, e.g. "H:Unit 4 …" or "L:04.5". */
    @NonNull
    public final String key;

    /** Unit title for TYPE_HEADER rows, otherwise empty. */
    @NonNull
    public final String headerTitle;

    /** Lesson for TYPE_LESSON rows, otherwise null. */
    @Nullable
    public final Lesson lesson;

    public final boolean completed;

    public final boolean bookmarked;

    /** Best quiz score, or -1 when the quiz was never attempted. */
    public final int bestQuizScore;

    private LessonRow(int type,
                      @NonNull String key,
                      @NonNull String headerTitle,
                      @Nullable Lesson lesson,
                      boolean completed,
                      boolean bookmarked,
                      int bestQuizScore) {
        this.type = type;
        this.key = key;
        this.headerTitle = headerTitle;
        this.lesson = lesson;
        this.completed = completed;
        this.bookmarked = bookmarked;
        this.bestQuizScore = bestQuizScore;
    }

    @NonNull
    public static LessonRow header(@NonNull String unitTitle) {
        return new LessonRow(TYPE_HEADER, "H:" + unitTitle, unitTitle,
                null, false, false, -1);
    }

    @NonNull
    public static LessonRow lesson(@NonNull Lesson lesson,
                                   @Nullable LessonProgressEntity progress) {
        return new LessonRow(
                TYPE_LESSON,
                "L:" + lesson.id,
                "",
                lesson,
                progress != null && progress.completed,
                progress != null && progress.bookmarked,
                progress == null ? -1 : progress.bestQuizScore);
    }

    /** Content equality for DiffUtil (identity is {@link #key}). */
    public boolean sameContent(@NonNull LessonRow other) {
        if (type != other.type) return false;
        if (type == TYPE_HEADER) return headerTitle.equals(other.headerTitle);
        return completed == other.completed
                && bookmarked == other.bookmarked
                && bestQuizScore == other.bestQuizScore;
    }
}
