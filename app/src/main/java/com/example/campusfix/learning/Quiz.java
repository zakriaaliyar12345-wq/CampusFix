package com.example.campusfix.learning;

import java.util.Arrays;
import java.util.List;

/** Tiny factories that keep the lesson-content files readable. */
final class Quiz {

    private Quiz() {
    }

    static QuizQuestion of(String question,
                           int correctIndex,
                           String explanation,
                           String... options) {
        return new QuizQuestion(
                question, Arrays.asList(options), correctIndex, explanation);
    }

    static List<QuizQuestion> list(QuizQuestion... questions) {
        return Arrays.asList(questions);
    }
}
