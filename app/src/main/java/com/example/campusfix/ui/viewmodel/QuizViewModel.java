package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.data.repository.LessonProgressRepository;
import com.example.campusfix.learning.Lesson;
import com.example.campusfix.learning.LessonCatalog;
import com.example.campusfix.learning.QuizQuestion;

import java.util.Collections;
import java.util.List;

/**
 * Quiz flow state: current question, selected option, grading and score.
 *
 * Lives in the ViewModel so a rotation never restarts the quiz
 * (topic 02.2). The answer cycle is select -> grade (shows the written
 * explanation) -> next, and the final grade persists the best score and
 * passes the lesson through {@link LessonProgressRepository#recordQuizScore}.
 */
public class QuizViewModel extends ViewModel {

    private final LessonProgressRepository repository;

    private final MutableLiveData<Boolean> finished = new MutableLiveData<>(false);

    private String lessonId;
    private List<QuizQuestion> questions = Collections.emptyList();

    private int index;
    private int score;
    private int selected = -1;
    private boolean graded;
    private boolean passed;

    public QuizViewModel(@NonNull LessonProgressRepository repository) {
        this.repository = repository;
    }

    /** Binds the quiz to a lesson; no-op when the same lesson is reloaded. */
    public void start(@NonNull String id) {
        if (id.equals(lessonId)) return;
        lessonId = id;
        Lesson lesson = LessonCatalog.byId(id);
        questions = lesson == null
                ? Collections.emptyList() : lesson.quiz;
        reset();
    }

    private void reset() {
        index = 0;
        score = 0;
        selected = -1;
        graded = false;
        passed = false;
        finished.setValue(false);
    }

    // ------------------------------------------------------------- reading

    @Nullable
    public String getLessonId() {
        return lessonId;
    }

    public int getTotal() {
        return questions.size();
    }

    /** 1-based position shown as "Question 1 of 3". */
    public int getPosition() {
        return Math.min(index + 1, questions.size());
    }

    @Nullable
    public QuizQuestion getCurrent() {
        return index < questions.size() ? questions.get(index) : null;
    }

    public int getSelected() {
        return selected;
    }

    public boolean isGraded() {
        return graded;
    }

    public boolean isFinished() {
        return Boolean.TRUE.equals(finished.getValue());
    }

    public boolean isLastQuestion() {
        return index == questions.size() - 1;
    }

    public int getScore() {
        return score;
    }

    public boolean isPassed() {
        return passed;
    }

    public LiveData<Boolean> getFinished() {
        return finished;
    }

    // ------------------------------------------------------------- writing

    /** Records the tapped option (only while the question is unanswered). */
    public void select(int optionIndex) {
        if (graded) return;
        selected = optionIndex;
    }

    /** Clears a radio selection after a rotation re-render. */
    public boolean hasSelection() {
        return selected >= 0;
    }

    /**
     * Grades the current answer and reveals its explanation.
     *
     * @return false when nothing has been selected yet.
     */
    public boolean grade() {
        if (graded || selected < 0) return false;
        QuizQuestion question = getCurrent();
        if (question != null && selected == question.correctIndex) {
            score++;
        }
        graded = true;
        return true;
    }

    /** Advances after the explanation; grades the final question into a pass. */
    public void next() {
        if (!graded) return;
        if (isLastQuestion()) {
            passed = repository.recordQuizScore(
                    lessonId, score, questions.size());
            finished.setValue(true);
        } else {
            index++;
            selected = -1;
            graded = false;
        }
    }

    /** Restarts the same quiz after a failed attempt. */
    public void retry() {
        if (!isFinished()) return;
        reset();
    }
}
