package com.example.campusfix.ui.learning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.learning.QuizQuestion;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.viewmodel.QuizViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * Lesson quiz: one question at a time with dynamically built radio options,
 * an explanation shown after grading, and a final score that is persisted to
 * Room when 60% or more is reached.
 *
 * Course topics demonstrated here: 04.2 (radio input), 02.2 (the flow state
 * survives rotation because it lives in the ViewModel), 04.4 (navigation).
 */
public class QuizActivity extends AppCompatActivity {

    private static final String EXTRA_LESSON_ID = "extra_lesson_id";

    private QuizViewModel viewModel;

    private TextView progressText;
    private TextView questionText;
    private View questionPanel;
    private RadioGroup optionGroup;
    private MaterialButton actionButton;
    private View feedbackPanel;
    private TextView feedbackVerdict;
    private TextView feedbackExplanation;
    private View resultPanel;
    private TextView resultScore;
    private TextView resultVerdict;

    public static void launch(@NonNull Context context,
                              @NonNull String lessonId) {
        Intent intent = new Intent(context, QuizActivity.class)
                .putExtra(EXTRA_LESSON_ID, lessonId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(QuizViewModel.class);

        String lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        if (lessonId == null) {
            Toast.makeText(this, R.string.lesson_not_found,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        viewModel.start(lessonId);

        bindViews();
        setupAction();

        if (viewModel.isFinished()) {
            renderResult();
        } else if (viewModel.isGraded()) {
            renderQuestion();
            renderGraded();
        } else {
            renderQuestion();
        }
    }

    private void bindViews() {
        progressText = findViewById(R.id.tvQuizProgress);
        questionText = findViewById(R.id.tvQuizQuestion);
        questionPanel = findViewById(R.id.quizQuestionPanel);
        optionGroup = findViewById(R.id.radioGroupOptions);
        actionButton = findViewById(R.id.btnQuizAction);
        feedbackPanel = findViewById(R.id.quizFeedbackPanel);
        feedbackVerdict = findViewById(R.id.tvQuizVerdict);
        feedbackExplanation = findViewById(R.id.tvQuizExplanation);
        resultPanel = findViewById(R.id.quizResultPanel);
        resultScore = findViewById(R.id.tvQuizResultScore);
        resultVerdict = findViewById(R.id.tvQuizResultVerdict);
    }

    private void setupAction() {
        actionButton.setOnClickListener(v -> {
            if (!viewModel.isGraded()) {
                if (!viewModel.grade()) {
                    Toast.makeText(this, R.string.quiz_select_answer,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                renderGraded();
                return;
            }
            viewModel.next();
            if (viewModel.isFinished()) {
                renderResult();
            } else {
                renderQuestion();
            }
        });

        findViewById(R.id.btnQuizRetry).setOnClickListener(v -> {
            viewModel.retry();
            renderQuestion();
        });

        findViewById(R.id.btnQuizDone).setOnClickListener(v -> finish());
    }

    /** Inflates the current question and its radio options dynamically. */
    private void renderQuestion() {
        questionPanel.setVisibility(View.VISIBLE);
        feedbackPanel.setVisibility(View.GONE);
        resultPanel.setVisibility(View.GONE);
        actionButton.setVisibility(View.VISIBLE);

        QuizQuestion question = viewModel.getCurrent();
        if (question == null) {
            finish();
            return;
        }

        progressText.setText(getString(R.string.quiz_progress,
                viewModel.getPosition(), viewModel.getTotal()));
        questionText.setText(question.question);

        optionGroup.setOnCheckedChangeListener(null);
        optionGroup.removeAllViews();
        for (int i = 0; i < question.options.size(); i++) {
            RadioButton button = new RadioButton(this);
            button.setId(View.generateViewId());
            button.setText(question.options.get(i));
            button.setPadding(button.getPaddingLeft(),
                    getResources().getDimensionPixelSize(R.dimen.spacing_small),
                    button.getPaddingRight(),
                    getResources().getDimensionPixelSize(R.dimen.spacing_small));
            optionGroup.addView(button);
        }
        optionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int index = group.indexOfChild(group.findViewById(checkedId));
            if (index >= 0) viewModel.select(index);
        });

        int selected = viewModel.getSelected();
        if (selected >= 0 && selected < optionGroup.getChildCount()) {
            optionGroup.check(optionGroup.getChildAt(selected).getId());
        }

        actionButton.setText(R.string.quiz_submit);
        actionButton.setEnabled(true);
    }

    /** Reveals the graded answer: verdict plus the written explanation. */
    private void renderGraded() {
        QuizQuestion question = viewModel.getCurrent();
        if (question == null) return;

        int selected = viewModel.getSelected();
        boolean correct = selected == question.correctIndex;

        optionGroup.setOnCheckedChangeListener(null);
        optionGroup.setEnabled(false);
        for (int i = 0; i < optionGroup.getChildCount(); i++) {
            optionGroup.getChildAt(i).setEnabled(false);
        }

        feedbackPanel.setVisibility(View.VISIBLE);
        feedbackVerdict.setText(correct
                ? R.string.quiz_answer_correct
                : R.string.quiz_answer_incorrect);
        feedbackVerdict.setTextColor(ContextCompat.getColor(this, correct
                ? R.color.status_resolved
                : R.color.error));
        feedbackExplanation.setText(question.explanation);

        actionButton.setText(viewModel.isLastQuestion()
                ? R.string.quiz_see_results
                : R.string.quiz_next_question);
    }

    private void renderResult() {
        questionPanel.setVisibility(View.GONE);
        feedbackPanel.setVisibility(View.GONE);
        actionButton.setVisibility(View.GONE);
        resultPanel.setVisibility(View.VISIBLE);

        resultScore.setText(getString(R.string.quiz_result_score,
                viewModel.getScore(), viewModel.getTotal()));
        resultVerdict.setText(viewModel.isPassed()
                ? R.string.quiz_result_pass
                : R.string.quiz_result_fail);
        resultVerdict.setTextColor(ContextCompat.getColor(this,
                viewModel.isPassed()
                        ? R.color.status_resolved
                        : R.color.error));
    }
}
