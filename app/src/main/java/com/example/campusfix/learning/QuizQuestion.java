package com.example.campusfix.learning;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Multiple-choice question attached to a lesson.
 *
 * The correct answer is stored as an index plus a written explanation that
 * is shown after grading, so wrong answers become learning moments.
 */
public class QuizQuestion {

    @NonNull
    public final String question;

    /** Two to four answer options; option 0 is always a real choice. */
    @NonNull
    public final List<String> options;

    /** Index into {@link #options} of the correct answer. */
    public final int correctIndex;

    /** Shown after grading: why the correct answer is correct. */
    @NonNull
    public final String explanation;

    public QuizQuestion(@NonNull String question,
                        @NonNull List<String> options,
                        int correctIndex,
                        @NonNull String explanation) {
        if (options.size() < 2) {
            throw new IllegalArgumentException(
                    "QuizQuestion needs at least 2 options: " + question);
        }
        if (correctIndex < 0 || correctIndex >= options.size()) {
            throw new IllegalArgumentException(
                    "correctIndex out of range: " + question);
        }
        this.question = question;
        this.options = Collections.unmodifiableList(options);
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }
}
