package com.example.campusfix.learning;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * One Android course topic turned into an in-app lesson.
 *
 * Immutable by design: lessons are declared once in the LessonCatalog and
 * never mutated, so they are safe to share across activities and easy to
 * assert on in unit tests (see LessonCatalogTest).
 */
public class Lesson {

    /** Course file number, e.g. "04.5" — also the Room primary key. */
    @NonNull
    public final String id;

    /** Exact topic title from the course material, e.g. "RecyclerView". */
    @NonNull
    public final String title;

    /** Grouping shown as section headers, e.g. "Unit 4 — Input & Navigation". */
    @NonNull
    public final String unit;

    /** One-sentence teaser shown in the lesson list. */
    @NonNull
    public final String summary;

    /**
     * Full lesson body: explanation paragraphs plus fenced code examples
     * (```java / ```xml blocks are rendered in a monospace face).
     */
    @NonNull
    public final String content;

    /** Hands-on task the reader performs in Android Studio or the app. */
    @NonNull
    public final String exercise;

    @NonNull
    public final List<QuizQuestion> quiz;

    /**
     * "W" = this topic is a working feature of CampusFix,
     * "D" = this topic is demonstrated through the lesson itself.
     */
    @NonNull
    public final String coverage;

    public Lesson(@NonNull String id,
                  @NonNull String title,
                  @NonNull String unit,
                  @NonNull String summary,
                  @NonNull String content,
                  @NonNull String exercise,
                  @NonNull List<QuizQuestion> quiz,
                  @NonNull String coverage) {
        this.id = id;
        this.title = title;
        this.unit = unit;
        this.summary = summary;
        this.content = content;
        this.exercise = exercise;
        this.quiz = Collections.unmodifiableList(quiz);
        this.coverage = coverage;
    }
}
