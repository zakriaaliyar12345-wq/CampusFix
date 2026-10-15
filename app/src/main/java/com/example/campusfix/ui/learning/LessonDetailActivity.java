package com.example.campusfix.ui.learning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.data.local.entity.LessonProgressEntity;
import com.example.campusfix.learning.Lesson;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.viewmodel.LessonDetailViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * Lesson reader: renders the lesson body with fenced ``` code blocks in a
 * monospace face, shows the practice exercise, and offers bookmark /
 * completion / quiz actions backed by Room LiveData.
 *
 * Course topics demonstrated here: 01.3 (scrollable text), 02.1 (explicit
 * intent to the quiz), 04.1 (click listeners), 10.1 (LiveData + ViewModel).
 */
public class LessonDetailActivity extends AppCompatActivity {

    private static final String EXTRA_LESSON_ID = "extra_lesson_id";

    private LessonDetailViewModel viewModel;

    private Lesson lesson;
    private LessonProgressEntity progress;

    private TextView title;
    private TextView summary;
    private LinearLayout content;
    private TextView progressInfo;
    private MaterialButton bookmarkButton;
    private MaterialButton completeButton;

    public static void launch(@NonNull Context context,
                              @NonNull String lessonId) {
        Intent intent = new Intent(context, LessonDetailActivity.class)
                .putExtra(EXTRA_LESSON_ID, lessonId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(LessonDetailViewModel.class);

        String lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        if (lessonId == null) {
            Toast.makeText(this, R.string.lesson_not_found,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.load(lessonId);
        lesson = viewModel.getLesson();
        if (lesson == null) {
            Toast.makeText(this, R.string.lesson_not_found,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        renderHeader();
        renderContent();
        renderExercise();
        setupActions();
        observe();
    }

    private void bindViews() {
        title = findViewById(R.id.tvLessonTitle);
        summary = findViewById(R.id.tvLessonSummary);
        content = findViewById(R.id.lessonContent);
        progressInfo = findViewById(R.id.tvLessonProgressInfo);
        bookmarkButton = findViewById(R.id.btnLessonBookmark);
        completeButton = findViewById(R.id.btnLessonComplete);
    }

    private void renderHeader() {
        setTitle(lesson.title);
        TextView lessonId = findViewById(R.id.tvLessonId);
        TextView lessonUnit = findViewById(R.id.tvLessonUnit);
        lessonId.setText(lesson.id);
        lessonUnit.setText(lesson.unit);
        title.setText(lesson.title);
        summary.setText(lesson.summary);
    }

    /**
     * Splits the lesson body on ``` fences: even segments are prose, odd
     * segments are code rendered in a monospace face on a tinted card.
     */
    private void renderContent() {
        content.removeAllViews();
        String[] segments = lesson.content.split("```", -1);
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.trim().isEmpty()) continue;
            content.addView(i % 2 == 0
                    ? proseView(segment)
                    : codeView(segment));
        }
    }

    private TextView proseView(String segment) {
        TextView view = new TextView(this);
        view.setText(formatProse(segment));
        view.setTextSize(getResources().getDimension(R.dimen.text_body)
                / getResources().getDisplayMetrics().scaledDensity);
        view.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        view.setLineSpacing(3f, 1f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = getResources()
                .getDimensionPixelSize(R.dimen.spacing_small);
        view.setLayoutParams(params);
        return view;
    }

    private TextView codeView(String segment) {
        TextView view = new TextView(this);
        view.setText(segment.trim());
        view.setTypeface(Typeface.MONOSPACE);
        view.setTextSize(13);
        view.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        view.setTextIsSelectable(true);
        view.setBackgroundResource(R.drawable.bg_code_block);
        int padding = getResources()
                .getDimensionPixelSize(R.dimen.spacing_small);
        view.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = padding;
        params.bottomMargin = padding;
        view.setLayoutParams(params);
        return view;
    }

    /** Turns the lesson's "* " bullet lines into proper bullets. */
    private String formatProse(String segment) {
        String[] lines = segment.split("\n", -1);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("* ")) {
                line = "\u2022 " + line.substring(2);
            }
            if (builder.length() > 0) builder.append('\n');
            builder.append(line);
        }
        return builder.toString().trim();
    }

    private void renderExercise() {
        TextView exercise = findViewById(R.id.tvLessonExercise);
        exercise.setText(lesson.exercise);
    }

    private void setupActions() {
        findViewById(R.id.btnLessonQuiz).setOnClickListener(v ->
                QuizActivity.launch(this, lesson.id));

        bookmarkButton.setOnClickListener(v -> viewModel.toggleBookmark());

        completeButton.setOnClickListener(v -> {
            boolean nowCompleted = !(progress != null && progress.completed);
            viewModel.setCompleted(nowCompleted);
        });
    }

    private void observe() {
        viewModel.getProgress().observe(this, entity -> {
            progress = entity;
            renderProgress();
        });
    }

    private void renderProgress() {
        boolean completed = progress != null && progress.completed;
        boolean bookmarked = progress != null && progress.bookmarked;

        bookmarkButton.setText(bookmarked
                ? R.string.lesson_bookmark_remove
                : R.string.lesson_bookmark_add);
        completeButton.setText(completed
                ? R.string.lesson_mark_incomplete
                : R.string.lesson_mark_complete);

        if (progress != null && progress.bestQuizScore >= 0) {
            progressInfo.setText(getString(
                    R.string.learning_best_score,
                    progress.bestQuizScore, lesson.quiz.size()));
        } else {
            progressInfo.setText(R.string.learning_quiz_not_attempted);
        }
    }
}
