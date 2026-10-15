package com.example.campusfix.ui.learning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.adapter.LessonAdapter;
import com.example.campusfix.ui.viewmodel.LearningViewModel;
import com.google.android.material.chip.ChipGroup;

/**
 * The Android Learning Lab: all 32 course topics grouped into units, with
 * persisted completion/bookmark progress from Room.
 *
 * Course topics demonstrated here: 01.2 (resources), 04.2 (filter chips),
 * 04.4 (navigation to the reader), 04.5 (RecyclerView + DiffUtil), 10.1
 * (LiveData observed from a ViewModel).
 */
public class LearningActivity extends AppCompatActivity {

    /** Intent extra: open directly on the Bookmarked filter. */
    public static final String EXTRA_SHOW_BOOKMARKS = "extra_show_bookmarks";

    private LearningViewModel viewModel;
    private LessonAdapter adapter;

    private TextView tvProgress;
    private View emptyState;
    private TextView tvEmptyTitle;
    private TextView tvEmptyBody;

    public static void launch(@NonNull Context context) {
        context.startActivity(new Intent(context, LearningActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(LearningViewModel.class);

        // Deep link from the dashboard's "Saved Lessons" button — applied
        // only on a fresh start so a rotated screen keeps the user's filter.
        if (savedInstanceState == null
                && getIntent().getBooleanExtra(EXTRA_SHOW_BOOKMARKS, false)) {
            viewModel.setMode(LearningViewModel.Mode.BOOKMARKS);
        }

        bindViews();
        setupList();
        setupFilterChips();
        observe();
    }

    private void bindViews() {
        tvProgress = findViewById(R.id.tvLearningProgress);
        emptyState = findViewById(R.id.learningEmptyState);
        tvEmptyTitle = findViewById(R.id.tvLearningEmptyTitle);
        tvEmptyBody = findViewById(R.id.tvLearningEmptyBody);
    }

    private void setupList() {
        adapter = new LessonAdapter(lesson ->
                LessonDetailActivity.launch(this, lesson.id));
        RecyclerView recycler = findViewById(R.id.recyclerLessons);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    private void setupFilterChips() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupLearning);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                group.check(R.id.chipLearnAll);
                return;
            }
            int id = checkedIds.get(0);
            if (id == R.id.chipLearnBookmarks) {
                viewModel.setMode(LearningViewModel.Mode.BOOKMARKS);
            } else if (id == R.id.chipLearnCompleted) {
                viewModel.setMode(LearningViewModel.Mode.COMPLETED);
            } else {
                viewModel.setMode(LearningViewModel.Mode.ALL);
            }
        });
    }

    private void observe() {
        viewModel.getRows().observe(this, rows -> {
            adapter.submitList(rows);
            boolean empty = rows == null || rows.isEmpty();
            emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            if (empty) renderEmpty();
        });

        viewModel.getCompletedCount().observe(this, completed -> {
            int done = completed == null ? 0 : completed;
            tvProgress.setText(getString(
                    R.string.learning_progress_count,
                    done, viewModel.getTotalLessons()));
        });

        viewModel.getMode().observe(this, mode -> {
            ChipGroup chipGroup = findViewById(R.id.chipGroupLearning);
            int expected = R.id.chipLearnAll;
            if (mode == LearningViewModel.Mode.BOOKMARKS) {
                expected = R.id.chipLearnBookmarks;
            } else if (mode == LearningViewModel.Mode.COMPLETED) {
                expected = R.id.chipLearnCompleted;
            }
            if (chipGroup.getCheckedChipId() != expected) {
                chipGroup.check(expected);
            }
        });
    }

    private void renderEmpty() {
        LearningViewModel.Mode mode = viewModel.getMode().getValue();
        if (mode == LearningViewModel.Mode.BOOKMARKS) {
            tvEmptyTitle.setText(R.string.learning_empty_bookmarks_title);
            tvEmptyBody.setText(R.string.learning_empty_bookmarks_body);
        } else if (mode == LearningViewModel.Mode.COMPLETED) {
            tvEmptyTitle.setText(R.string.learning_empty_completed_title);
            tvEmptyBody.setText(R.string.learning_empty_completed_body);
        } else {
            tvEmptyTitle.setText(R.string.learning_empty_title);
            tvEmptyBody.setText(R.string.learning_empty_body);
        }
    }
}
